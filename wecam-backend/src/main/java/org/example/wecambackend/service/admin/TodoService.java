package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.council.Council;
import org.example.model.enums.ProgressStatus;
import org.example.model.todo.Todo;
import org.example.model.todo.TodoFile;
import org.example.model.todo.TodoManager;
import org.example.model.user.User;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.Enum.TodoTypeDTO;
import org.example.wecambackend.dto.projection.ManagerInfo;
import org.example.wecambackend.dto.projection.TodoFileInfo;
import org.example.wecambackend.dto.requestDTO.TodoCreateRequest;
import org.example.wecambackend.dto.requestDTO.TodoUpdateRequest;
import org.example.wecambackend.dto.responseDTO.AdminFileResponse;
import org.example.wecambackend.dto.responseDTO.TodoDetailResponse;
import org.example.wecambackend.dto.responseDTO.TodoSimpleResponse;
import org.example.wecambackend.dto.responseDTO.TodoSummaryResponse;
import org.example.wecambackend.repos.*;
import org.example.wecambackend.service.admin.Enum.UploadFolder;
import org.example.wecambackend.service.admin.common.AdminFileStorageService;
import org.example.wecambackend.service.admin.common.EntityFinderService;
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


    /**
     * [설명]
     * - 새로운 할 일(Todo)을 생성합니다.
     * - 작성자 정보, 담당자 목록(todoManager), 첨부파일(todoFile)을 함께 저장합니다.
     * [필요한 변수]
     * - councilId: 할 일이 소속될 학생회 ID
     * - request: 제목, 내용, 마감일, 담당자 ID 리스트가 포함된 요청 DTO
     * - files: 업로드할 파일 리스트 (nullable)
     * - userId: 로그인한 사용자 (작성자)의 ID
     * [반환값]
     * - void
     */
    // 1.  엔티티 저장 (userId, title, content, dueAt 등)
    // 2.  todo_manager 테이블에 request.getManagerIds() 리스트 insert
    // 3.  업로드 후 todo_file 테이블에 파일 정보 저장
    @Transactional
    public void createTodo(Long councilId,TodoCreateRequest request, List<MultipartFile> files, Long userId ) {
        User user = entityFinderService.getUserByIdOrThrow(userId);
        Council council = entityFinderService.getCouncilByIdOrThrow(councilId);
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
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_NOT_FOUND));

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


    /**
     * [설명]
     * - 기존 할 일을 수정합니다.
     * - 제목/내용/마감일 변경, 담당자 변경, 파일 삭제 및 추가를 처리합니다.
     * [필요한 변수]
     * - todoId: 수정할 할 일의 ID
     * - councilId: 해당 할 일이 속한 학생회 ID
     * - request: 수정 요청 정보 (제목, 내용, 마감일, 담당자 목록, 삭제할 파일 ID)
     * - newFiles: 새로 추가할 파일 리스트
     * [반환값]
     * - void
     */

    @Transactional
    public void updateTodo(Long todoId, Long councilId, TodoUpdateRequest request, List<MultipartFile> newFiles) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REQUEST_NOT_FOUND));

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
                        .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_NOT_FOUND));

                TodoManager managerEntity = TodoManager.of(todo, manager);
                todoManagerRepository.save(managerEntity);
            }
        }

        // 3. 첨부파일 삭제 요청 처리
        if (request.getDeleteFileIds() != null && !request.getDeleteFileIds().isEmpty()) {
            for (UUID fileId : request.getDeleteFileIds()) {
                TodoFile todoFile = todoFileRepository.findByTodoFileId(fileId)
                        .orElseThrow(() -> new BaseException(BaseResponseStatus.FILE_NOT_FOUND));
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

    /*
     [설명]
     특정 할 일의 담당자 목록을 업데이트합니다.
     새 목록과 비교하여 기존 담당자를 추가하거나 삭제합니다.

     [필요한 변수]
     todo: 대상 할 일 엔티티
     newManagerIds: 새로운 담당자 ID 리스트
     councilId: 해당 할 일이 소속된 학생회 ID (담당자 유효성 검증용)
     [반환값]
     void
     [호출 위치 / 사용 예시]
     updateTodo 내부에서 담당자 목록 변경 시 사용
     */
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
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_MISMATCH));
            TodoManager newManager = TodoManager.of(todo, manager);
            todoManagerRepository.save(newManager);
        }
    }

    /**
     * [설명]
     * - 특정 할 일의 상세 정보를 조회합니다.
     * - 기본 정보, 작성자, 담당자, 첨부파일 목록을 포함합니다.
     * [필요한 변수]
     * - todoId: 조회할 할 일의 ID
     * [반환값]
     * - TodoDetailResponse: 할 일 상세 정보 DTO
     */
    @Transactional
    public TodoDetailResponse getTodoDetail(Long todoId) {
        Todo todo = todoRepository.findTodoWithCreator(todoId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.ENTITY_NOT_FOUND));

        List<ManagerInfo> managers = todoManagerRepository.findManagersByTodoId(todoId);

        Long createUserId =todo.getCreateUser().getUserPkId();

        String createUserName = userRepository.findNameByUserPkId(createUserId);

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


    /**
     [설명]
     사용자가 담당자인 할 일의 상태(진행도)를 변경합니다.
     [필요한 변수]
     todoId: 대상 할 일 ID
     userId: 요청을 보낸 사용자 ID
     newStatus: 변경할 진행 상태값 (enum)
     [반환값]
     void
     [호출 위치 / 사용 예시]
     할 일 목록에서 상태 버튼 클릭 시
     */
    @Transactional
    public void updateTodoStatus(Long todoId,Long userId, ProgressStatus newStatus) {
        boolean isManager = todoManagerRepository.existsByTodo_TodoIdAndUser_UserPkId(todoId, userId);
        if (!isManager) {
            throw new BaseException(BaseResponseStatus.ONLY_AUTHOR_CAN_MODIFY);
        }

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REQUEST_NOT_FOUND));
        todo.setProgressStatus(newStatus);
    }

    //TODO: file 삭제 까먹음.
    /**
     [설명]
     할 일을 삭제합니다. 단, 작성자 본인만 삭제할 수 있습니다.
     연관된 담당자 및 첨부파일은 cascade 및 orphanRemoval로 자동 삭제됩니다.
     [필요한 변수]
     todoId: 삭제할 할 일 ID
     userId: 요청을 보낸 사용자 ID
     [반환값]
     void
     [호출 위치 / 사용 예시]
     할 일 상세화면 > 삭제 버튼 클릭 시
     */
    @Transactional
    public void deleteTodo(Long todoId , Long userId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REQUEST_NOT_FOUND));
        // 작성자 본인 확인 (보안)
        if (!todo.getCreateUser().getUserPkId().equals(userId)) {
            throw new BaseException(BaseResponseStatus.ONLY_AUTHOR_CAN_MODIFY);
        }
        // 실제 삭제 (cascade + orphanRemoval 덕분에 하위 테이블 자동 삭제됨)
        todoRepository.delete(todo);
    }



    private final UserInformationRepository userInformationRepository;

    /**
     [설명]
     로그인한 사용자가 직접 작성하고, 담당자 중 하나이기도 한 할 일 목록을 조회합니다.
     [필요한 변수]
     userId: 로그인한 사용자 ID
     councilId: 학생회 ID
     [반환값]
     List<TodoSimpleResponse>: 간단한 할 일 요약 리스트
     [호출 위치 / 사용 예시]
     대시보드 > "내 할 일" 목록 요청 시
     */
    //담당자 작성자 모두 나인 거.
    public List<TodoSimpleResponse> getMyTodoList(Long userId,Long councilId) {
        List<Todo> todos = todoRepository.findAllByCreateUser_UserPkIdAndManagers_User_UserPkIdAndCouncil_Id(userId, userId,councilId);
        return todos.stream()
                .map(todo -> convertToTodoSimpleResponse(todo,TodoTypeDTO.MY_TODO))
                .collect(Collectors.toList());
    }

    /**
     [설명]
     로그인한 사용자가 담당자인 할 일 중, 작성자가 본인이 아닌 경우(받은 일)만 필터링하여 조회합니다.
     [필요한 변수]
     userId: 로그인한 사용자 ID
     councilId: 학생회 ID
     [반환값]
     List<TodoSimpleResponse>: 받은 할 일 리스트
     [호출 위치 / 사용 예시]
     대시보드 > "받은 할 일" 탭 조회 시
     */
    public List<TodoSimpleResponse> getReceivedTodoList(Long userId,Long councilId) {
        List<Todo> todos = todoRepository.findAllByManagers_User_UserPkIdAndCouncil_Id(userId,councilId);
        return todos.stream()
                .filter(todo -> !todo.getCreateUser().getUserPkId().equals(userId)) // 작성자가 내가 아닌 것만
                .map(todo -> convertToTodoSimpleResponse(todo, TodoTypeDTO.RECEIVED_TODO))
                .collect(Collectors.toList());
    }

    /**
     [설명]
     로그인한 사용자가 작성한 할 일 중, 담당자가 본인만이 아닌 경우(보낸 일)을 조회합니다.
     [필요한 변수]
     userId: 로그인한 사용자 ID
     councilId: 학생회 ID
     [반환값]
     List<TodoSimpleResponse>: 보낸 할 일 리스트
     [호출 위치 / 사용 예시]
     대시보드 > "보낸 할 일" 탭 조회 시
     */
    //보낸건데 담당자는 내가 아님.
    public List<TodoSimpleResponse> getSentTodoList(Long userId,Long councilId) {
        List<Todo> todos = todoRepository.findAllByCreateUser_UserPkIdAndCouncil_Id(userId,councilId);
        return todos.stream()
                .filter(todo -> !todo.getManagers().stream().allMatch(m -> m.getUser().getUserPkId().equals(userId))) // 담당자 전원이 나만이 아님
                .map(todo -> convertToTodoSimpleResponse(todo, TodoTypeDTO.SENT_TODO
                ))
                .collect(Collectors.toList());
    }


    /**
     [설명]
     로그인한 사용자의 할 일 전체를 한 번에 불러옵니다.
     내 할 일, 받은 일, 보낸 일을 모두 합쳐 중복 제거 후 반환합니다.
     [필요한 변수]
     userId: 로그인한 사용자 ID
     councilId: 학생회 ID
     [반환값]
     List<TodoSimpleResponse>: 전체 할 일 목록
     [호출 위치 / 사용 예시]
     대시보드 > 할 일 전체 보기 요청 시
     */
    @Transactional(readOnly = true)
    public List<TodoSimpleResponse> getAllTodoList(Long userId, Long councilId, TodoTypeDTO todoType, ProgressStatus progressStatus) {
        Set<TodoSimpleResponse> allTodos = new HashSet<>(); // 중복 제거

        // todo type에 따라 조회
        if (todoType == null || todoType == TodoTypeDTO.ALL_TODO || todoType == TodoTypeDTO.MY_TODO) {
            allTodos.addAll(getMyTodoList(userId, councilId));
        }
        if (todoType == null || todoType == TodoTypeDTO.ALL_TODO || todoType == TodoTypeDTO.SENT_TODO) {
            allTodos.addAll(getSentTodoList(userId, councilId));
        }
        if (todoType == null || todoType == TodoTypeDTO.ALL_TODO || todoType == TodoTypeDTO.RECEIVED_TODO) {
            allTodos.addAll(getReceivedTodoList(userId, councilId));
        }

        return allTodos.stream()
                .filter(todo -> {
                    if (progressStatus == null) return true;
                    if (progressStatus == ProgressStatus.DUE_TODAY) { // 오늘까지
                        return todo.getProgressStatus() != ProgressStatus.COMPLETED &&
                                todo.getDueAt() != null &&
                                !todo.getDueAt().isAfter(LocalDate.now().atTime(LocalTime.MAX)); // 오늘까지
                    }
                    return todo.getProgressStatus() == progressStatus;
                })
                .sorted(Comparator.comparing(TodoSimpleResponse::getDueAt, Comparator.nullsLast(Comparator.naturalOrder()))) // dueAt이 null이면 가장 마지막으로
                .toList();
    }

    private TodoSimpleResponse convertToTodoSimpleResponse(Todo todo,TodoTypeDTO type) {

        Long createUserId =todo.getCreateUser().getUserPkId();
        Long todoId = todo.getTodoId();
        String createUserName = userRepository.findNameByUserPkId(createUserId);
        List<ManagerInfo> managers = todoManagerRepository.findManagersByTodoId(todoId);

        return new TodoSimpleResponse(
                todoId,
                todo.getTitle(),
                todo.getContent(),
                todo.getDueAt(),
                managers,
                createUserId,
                createUserName,
                type,
                todo.getProgressStatus()
        );
    }


    /**
     * [설명]
      - 오늘, 이번 주, 받은 일, 보낸 일에 대한 할 일 통계 요약을 제공합니다.
      - 각 카테고리에 대해 전체 개수와 완료된 개수를 반환합니다.
      [필요한 변수]
      - councilId: 학생회 ID
      - userId: 로그인한 사용자 ID
     * [반환값]
      - TodoSummaryResponse: 오늘/이번 주/받은 일/보낸 일 요약 DTO
     */
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

    private final EntityFinderService entityFinderService;
}
