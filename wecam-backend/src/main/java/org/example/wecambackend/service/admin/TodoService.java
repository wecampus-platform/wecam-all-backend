package org.example.wecambackend.service.admin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.council.Council;
import org.example.model.enums.ProgressStatus;
import org.example.model.todo.Todo;
import org.example.model.todo.TodoFile;
import org.example.model.todo.TodoManager;
import org.example.model.user.User;
import org.example.wecambackend.dto.Enum.TodoTypeDTO;
import org.example.wecambackend.dto.projection.ManagerInfo;
import org.example.wecambackend.dto.projection.TodoFileInfo;
import org.example.wecambackend.dto.requestDTO.TodoCreateRequest;
import org.example.wecambackend.dto.requestDTO.TodoUpdateRequest;
import org.example.wecambackend.dto.responseDTO.AdminFileResponse;
import org.example.wecambackend.dto.responseDTO.TodoDetailResponse;
import org.example.wecambackend.dto.responseDTO.TodoSimpleResponse;
import org.example.wecambackend.dto.responseDTO.TodoSummaryResponse;
import org.example.wecambackend.exception.UnauthorizedException;
import org.example.wecambackend.repos.*;
import org.example.wecambackend.service.admin.Enum.UploadFolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {



    private final TodoRepository todoRepository;
    private final TodoManagerRepository todoManagerRepository;
    private final UserRepository userRepository;
    private final AdminFileStorageService adminFileStorageService;
    private final TodoFileRepository todoFileRepository;
    private final CouncilMemberRepository councilMemberRepository;
    private final CouncilRepository councilRepository;

    // 1.  엔티티 저장 (userId, title, content, dueAt 등)
    // 2.  todo_manager 테이블에 request.getManagerIds() 리스트 insert
    // 3.  업로드 후 todo_file 테이블에 파일 정보 저장
    @Transactional
    public void createTodo(Long councilId,TodoCreateRequest request, List<MultipartFile> files, Long userId ) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("유저가 존재하지 않습니다."));
        Council council = councilRepository.findById(councilId)
                .orElseThrow(()-> new IllegalArgumentException("학생회가 존재하지 않습니다"));
        Todo todo = Todo.builder()
                .title(request.getTitle())
                .content(String.valueOf(request.getContent()))
                .dueAt(request.getDueAt())
                .createUser(user)
                .council(council)
                .progressStatus(ProgressStatus.NOT_STARTED) // 기본 상태
                .build();

        todoRepository.save(todo);

        List<Long> managers = request.getManagers();
        if (managers.isEmpty() ) {
            TodoManager managerEntity = TodoManager.of(todo, user);
            todoManagerRepository.save(managerEntity);
        }
        else {
        // 2. 담당자들 저장 (todo_manager)
        for (Long managerId : request.getManagers()) {
            User manager = councilMemberRepository.findUserByUserUserPkIdAndCouncil_IdAndIsActiveTrue(managerId,councilId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저는"+councilId+"에 포함되지 않은 유저입니다. id: " + managerId));

            TodoManager managerEntity = TodoManager.of(todo, manager);

            todoManagerRepository.save(managerEntity);
        }
        }

        // 3. 파일 저장 (optional)
        if (files != null && !files.isEmpty()) {
            List<AdminFileResponse> uploadedFiles = adminFileStorageService.saveFiles(files, UploadFolder.TODO);

            for (AdminFileResponse file : uploadedFiles) {
                TodoFile todoFile = TodoFile.builder()
                        .todoFileId(UUID.randomUUID())
                        .todo(todo)
                        .originalFileName(file.getOriginalFileName())
                        .storedFileName(file.getStoredFileName())
                        .filePath(file.getFilePath())
                        .fileUrl(file.getUrl())
                        .build();
                todoFileRepository.save(todoFile);
            }

        }
    }


    @Transactional
    public void updateTodo(Long todoId, Long councilId, TodoUpdateRequest request, List<MultipartFile> newFiles) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 할일이 존재하지 않습니다."));

        // 1. 제목, 내용, 마감일 업데이트
        todo.update(request.getTitle(), String.valueOf(request.getContent()), request.getDueAt());

        // 2. 담당자 변경
        updateTodoManagers(todo,request.getManagers(),councilId);

        List<Long> managers = request.getManagers();
        if (managers == null || managers.isEmpty()) {
            // 기본 작성자만 담당자로 지정
            TodoManager managerEntity = TodoManager.of(todo, todo.getCreateUser());
            todoManagerRepository.save(managerEntity);
        } else {
            for (Long managerId : managers) {
                User manager = councilMemberRepository.findUserByUserUserPkIdAndCouncil_IdAndIsActiveTrue(managerId, councilId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 유저는 " + councilId + "에 포함되지 않은 유저입니다. id: " + managerId));

                TodoManager managerEntity = TodoManager.of(todo, manager);
                todoManagerRepository.save(managerEntity);
            }
        }

        // 3. 첨부파일 삭제 요청 처리
        if (request.getDeleteFileIds() != null && !request.getDeleteFileIds().isEmpty()) {
            for (UUID fileId : request.getDeleteFileIds()) {
                TodoFile todoFile = todoFileRepository.findByTodoFileId(fileId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파일입니다."));
                adminFileStorageService.deleteFile(todoFile.getFilePath()); // 물리적 삭제
                todoFileRepository.delete(todoFile); // DB 삭제
            }
        }

        // 4. 새 파일 업로드 처리
        if (newFiles != null && !newFiles.isEmpty()) {
            List<AdminFileResponse> uploadedFiles = adminFileStorageService.saveFiles(newFiles, UploadFolder.TODO);
            for (AdminFileResponse file : uploadedFiles) {
                TodoFile todoFile = TodoFile.builder()
                        .todoFileId(UUID.randomUUID())
                        .todo(todo)
                        .originalFileName(file.getOriginalFileName())
                        .storedFileName(file.getStoredFileName())
                        .filePath(file.getFilePath())
                        .fileUrl(file.getUrl())
                        .build();
                todoFileRepository.save(todoFile);
            }
        }
    }


    public void updateTodoManagers(Todo todo, List<Long> newManagerIds, Long councilId) {
        // 기존 매니저 ID들 조회
        List<TodoManager> existingManagers = todoManagerRepository.findByTodo_TodoId(todo.getTodoId());
        Set<Long> existingManagerIds = existingManagers.stream()
                .map(tm -> tm.getUser().getUserPkId())
                .collect(Collectors.toSet());

        Set<Long> newManagerIdSet = new HashSet<>(newManagerIds);

        // 추가해야 할 매니저 = 새 목록에 있지만 기존에는 없던 것
        Set<Long> toAdd = new HashSet<>(newManagerIdSet);
        toAdd.removeAll(existingManagerIds);

        // 제거해야 할 매니저 = 기존 목록에는 있지만 새 목록에는 없는 것
        Set<Long> toRemove = new HashSet<>(existingManagerIds);
        toRemove.removeAll(newManagerIdSet);

        // 삭제
        for (Long removeId : toRemove) {
            todoManagerRepository.deleteByTodoAndUserUserPkId(todo, removeId);
        }

        // 추가
        for (Long addId : toAdd) {
            User manager = councilMemberRepository
                    .findUserByUserUserPkIdAndCouncil_IdAndIsActiveTrue(addId, councilId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저는 이 학생회에 속하지 않습니다: " + addId));
            TodoManager newManager = TodoManager.of(todo, manager);
            todoManagerRepository.save(newManager);
        }
    }


    @Transactional
    public TodoDetailResponse getTodoDetail(Long todoId) {
        Todo todo = todoRepository.findTodoWithCreator(todoId)
                .orElseThrow(() -> new EntityNotFoundException("할일 없음"));

        List<ManagerInfo> managers = todoManagerRepository.findManagersByTodoId(todoId);

        Long createUserId =todo.getCreateUser().getUserPkId();

        String createUserName = userInformationRepository.findNameByUserId(createUserId);

        List<TodoFileInfo> files = todoFileRepository.findByTodo(todo).stream()
                .map(f -> new TodoFileInfo(f.getTodoFileId(), f.getOriginalFileName(), f.getFileUrl()))
                .collect(Collectors.toList());

        return new TodoDetailResponse(
                todo.getTodoId(),
                todo.getTitle(),
                todo.getContent(),
                todo.getDueAt(),
                todo.getProgressStatus(),
                managers,
                createUserId,
                createUserName,
                files
        );
    }
    //TODO: 다운로드 API 는 추후 구현


    @Transactional
    public void updateTodoStatus(Long todoId,Long userId, ProgressStatus newStatus) {
        boolean isManager = todoManagerRepository.existsByTodo_TodoIdAndUser_UserPkId(todoId, userId);
        if (!isManager) {
            throw new UnauthorizedException("해당 할일의 매니저만 상태를 변경할 수 있습니다.");
        }

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("해당 할 일이 존재하지 않습니다."));
        todo.setProgressStatus(newStatus);
    }

    @Transactional
    public void deleteTodo(Long todoId , Long userId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("할일을 찾을 수 없습니다."));

        // 작성자 본인 확인 (보안)
        if (!todo.getCreateUser().getUserPkId().equals(userId)) {
            throw new UnauthorizedException("할일 작성자만 삭제할 수 있습니다.");
        }
        // 실제 삭제 (cascade + orphanRemoval 덕분에 하위 테이블 자동 삭제됨)
        todoRepository.delete(todo);
    }



    private final UserInformationRepository userInformationRepository;

    //담당자 작성자 모두 나인 거.
    public List<TodoSimpleResponse> getMyTodoList(Long userId,Long councilId) {
        List<Todo> todos = todoRepository.findAllByCreateUser_UserPkIdAndManagers_User_UserPkIdAndCouncil_Id(userId, userId,councilId);
        return todos.stream()
                .map(todo -> convertToTodoSimpleResponse(todo,TodoTypeDTO.MY_TODO))
                .collect(Collectors.toList());
    }

    public List<TodoSimpleResponse> getReceivedTodoList(Long userId,Long councilId) {
        List<Todo> todos = todoRepository.findAllByManagers_User_UserPkIdAndCouncil_Id(userId,councilId);
        return todos.stream()
                .filter(todo -> !todo.getCreateUser().getUserPkId().equals(userId)) // 작성자가 내가 아닌 것만
                .map(todo -> convertToTodoSimpleResponse(todo, TodoTypeDTO.RECEIVED_TODO))
                .collect(Collectors.toList());
    }

    //보낸건데 담당자는 내가 아님.
    public List<TodoSimpleResponse> getSentTodoList(Long userId,Long councilId) {
        List<Todo> todos = todoRepository.findAllByCreateUser_UserPkIdAndCouncil_Id(userId,councilId);
        return todos.stream()
                .filter(todo -> !todo.getManagers().stream().allMatch(m -> m.getUser().getUserPkId().equals(userId))) // 담당자 전원이 나만이 아님
                .map(todo -> convertToTodoSimpleResponse(todo, TodoTypeDTO.SENT_TODO
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TodoSimpleResponse> getAllTodoList(Long userId,Long councilId) {
         Set<TodoSimpleResponse> allTodos = new HashSet<>();
            allTodos.addAll(getMyTodoList(userId,councilId));
            allTodos.addAll(getSentTodoList(userId,councilId));
            allTodos.addAll(getReceivedTodoList(userId,councilId));

            return new ArrayList<>(allTodos);
    }


    private TodoSimpleResponse convertToTodoSimpleResponse(Todo todo,TodoTypeDTO type) {


//        TodoTypeDTO type;
        Long createUserId =todo.getCreateUser().getUserPkId();
        Long todoId = todo.getTodoId();
        String createUserName = userInformationRepository.findNameByUserId(createUserId);
        List<ManagerInfo> managers = todoManagerRepository.findManagersByTodoId(todoId);
//        boolean isCreator = createUserId.equals(currentUserId);
//        boolean isManager = todoManagerRepository.existsByTodo_TodoIdAndUser_UserPkId(todoId,currentUserId);

//        if (isCreator && isManager) {
//            type = TodoTypeDTO.MY_TODO;
//        } else if (isCreator) {
//            type = TodoTypeDTO.SENT_TODO;
//        } else if (isManager) {
//            type = TodoTypeDTO.RECEIVED_TODO;
//        } else {
//            type = null; // 예외적으로 들어올 경우
//        }


        return new TodoSimpleResponse(
                todoId,
                todo.getTitle(),
                todo.getContent(),
                todo.getDueAt(),
                todo.getProgressStatus(),
                managers,
                createUserId,
                createUserName,
                type
        );
    }


    @Transactional
    public TodoSummaryResponse getTodoSummary(Long councilId, Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = now.toLocalDate().atTime(LocalTime.MAX);
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        LocalDateTime weekStart = startOfWeek.atStartOfDay();
        LocalDateTime weekEnd = endOfWeek.atTime(LocalTime.MAX);

        // 오늘 할 일
        List<Todo> todayTodos = todoRepository.findByCouncil_IdAndManagers_User_UserPkIdAndDueAtBetween(
                councilId, userId, todayStart,todayEnd);
        int todayTotal = todayTodos.size();
        int todayDone = (int) todayTodos.stream()
                .filter(todo -> todo.getProgressStatus() == ProgressStatus.COMPLETED) // 완료한 일
                .count();

        // 이번 주 할 일
        List<Todo> weekTodos = todoRepository.findByCouncil_IdAndManagers_User_UserPkIdAndDueAtBetween(
                councilId, userId, weekStart, weekEnd);
        int weekTotal = weekTodos.size();
        int weekDone = (int) todayTodos.stream()
                .filter(todo -> todo.getProgressStatus() == ProgressStatus.COMPLETED) // 완료한 일
                .count();
        int weekRate = (weekTotal == 0) ? 0 : (int) ((double) weekDone / weekTotal * 100);

        // 받은 일 _ 담당자가 나인 학생회 할일
        List<Todo> received = todoRepository.findAllByManagers_User_UserPkIdAndCouncil_Id(userId, councilId);
        int receivedTotal = received.size();
        int receivedDone = (int) todayTodos.stream()
                .filter(todo -> todo.getProgressStatus() == ProgressStatus.COMPLETED) // 완료한 일
                .count();

        // 보낸 일
        List<Todo> sent = todoRepository.findAllByCreateUser_UserPkIdAndCouncil_Id(userId,councilId);
        int sentTotal = sent.size();
        int sentDone =(int) todayTodos.stream()
                .filter(todo -> todo.getProgressStatus() == ProgressStatus.COMPLETED) // 완료한 일
                .count();

        return TodoSummaryResponse.builder()
                .todayTodo(new TodoSummaryResponse.CountPair(todayDone, todayTotal))
                .weekTodo(new TodoSummaryResponse.RateTriple(weekDone, weekTotal, weekRate))
                .receivedTodo(new TodoSummaryResponse.CountPair(receivedDone, receivedTotal))
                .sentTodo(new TodoSummaryResponse.CountPair(sentDone, sentTotal))
                .build();
    }
}
