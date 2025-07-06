package org.example.wecambackend.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.enums.ProgressStatus;
import org.example.model.todo.Todo;
import org.example.model.todo.TodoFile;
import org.example.model.todo.TodoManager;
import org.example.model.user.User;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.dto.requestDTO.TodoCreateRequest;
import org.example.wecambackend.dto.responseDTO.AdminFileResponse;
import org.example.wecambackend.repos.*;
import org.example.wecambackend.service.admin.Enum.UploadFolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {


    // 1.  엔티티 저장 (userId, title, content, dueAt 등)
    // 2. todo_manager 테이블에 request.getManagerIds() 리스트 insert
    // 3. S3 업로드 후 todo_file 테이블에 파일 정보 저장
    @Transactional
    public void createTodo(Long councilId,TodoCreateRequest request, List<MultipartFile> files, Long userId ) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("유저가 존재하지 않습니다."));
        Todo todo = Todo.builder()
                .title(request.getTitle())
                .content(String.valueOf(request.getContent()))
                .dueAt(request.getDueAt())
                .createUser(user)
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

    private final TodoRepository todoRepository;
    private final TodoManagerRepository todoManagerRepository;
    private final UserRepository userRepository;
    private final AdminFileStorageService adminFileStorageService;
    private final TodoFileRepository todoFileRepository;
    private final CouncilMemberRepository councilMemberRepository;
}
