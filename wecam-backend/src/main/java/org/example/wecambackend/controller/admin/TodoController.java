package org.example.wecambackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.model.enums.ProgressStatus;
import org.example.model.todo.Todo;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.common.response.BaseResponse;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.CheckOwner;
import org.example.wecambackend.config.security.annotation.CheckTodoAccess;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.Enum.TodoTypeDTO;
import org.example.wecambackend.dto.request.todo.TodoCreateRequest;
import org.example.wecambackend.dto.request.todo.TodoStatusUpdateRequest;
import org.example.wecambackend.dto.request.todo.TodoUpdateRequest;
import org.example.wecambackend.dto.response.todo.TodoDetailResponse;
import org.example.wecambackend.dto.response.todo.TodoSimpleResponse;
import org.example.wecambackend.dto.response.todo.TodoSummaryResponse;
import org.example.wecambackend.service.admin.TodoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;



@RequiredArgsConstructor
@RestController
@Tag(name = "Todo Controller" , description = " 학생회 Todo Controller ")
@RequestMapping("admin/council/{councilName}/todo")
public class TodoController {

    private final TodoService todoService;
    @IsCouncil
    @Operation(
            summary = "할일 등록",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "councilId", description = "학생회 PK", in = ParameterIn.PATH),
                    @Parameter(name = "councilName", description = "학생회 이름", in = ParameterIn.PATH)
            }
    )
    @PostMapping(value = "/{councilId}/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity<?> createTodo(
            @PathVariable("councilId") Long councilId,
            @PathVariable("councilName") String councilName,
            @RequestPart("request") @Valid TodoCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // ✅ 로그 출력
        System.out.println("📝 [POST /create] 할 일 등록 요청 도착");
        System.out.println("    🔸 유저 ID: " + userDetails.getId());
        System.out.println("    🔸 학생회 이름: " + councilName);
        System.out.println("    🔸 학생회 ID: " + councilId);
        System.out.println("    🔸 제목: " + request.getTitle());
        System.out.println("    🔸 마감일: " + request.getDueAt());
        System.out.println("    🔸 담당자 수: " + (request.getManagers() != null ? request.getManagers().size() : 0));
        System.out.println("    🔸 첨부파일 수: " + (files != null ? files.size() : 0));

        // 실제 로직 실행
        todoService.createTodo(councilId, request, files, userDetails.getId());

        System.out.println("✅ 할 일 등록 완료");
        return ResponseEntity.ok("할일 등록이 완료되었습니다.");
    }


    @IsCouncil // 접속한 유저가 선택한 학생회 관리지 페이지가 맞는지 (프론트에서 주는 councilId 와 Redis 에 저장해두었던 학생회 접속 Id 비교)
    @CheckOwner(entity = Todo.class, idParam = "todoId", authorGetter = "getCreateUser.getUserPkId")
    @PutMapping("/{todoId}/edit")
    @Operation(summary = "할 일 수정 _ 기존 값에서 수정 , 작성자만 가능",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)}
    )
    public ResponseEntity<?> updateTodo(
            @PathVariable Long todoId,
            @RequestPart("request") @Valid TodoUpdateRequest request,
            @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String councilName

    ) {
        Long councilId = CouncilContextHolder.getCouncilId();
        todoService.updateTodo(todoId, councilId, request, newFiles);
        return ResponseEntity.ok("할일 수정 완료");
    }

    @IsCouncil // 접속한 유저가 선택한 학생회 관리지 페이지가 맞는지 (프론트에서 주는 councilId 와 Redis 에 저장해두었던 학생회 접속 Id 비교)
    @CheckTodoAccess(idParam = "todoId")
    @GetMapping("/{todoId}")
    @Operation(summary = "할 일 상세 조회",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)}
    )
    public ResponseEntity<TodoDetailResponse> getTodoDetail(@PathVariable Long todoId,
                                                            @PathVariable String councilName,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails

                                                            ) {
        TodoDetailResponse response = todoService.getTodoDetail(todoId);
        System.out.println("💬 [GET /DETAIL] 요청 도착");
        System.out.println("    🔸 유저 ID: " + userDetails.getId());
        System.out.println("    🔸 학생회 이름: " + councilName);
        System.out.println("    🔸 요청온 todoId: " + todoId);
        System.out.println("    🔸 response: " + response);
        return ResponseEntity.ok(response);
    }

    @IsCouncil // 접속한 유저가 선택한 학생회 관리지 페이지가 맞는지 (프론트에서 주는 councilId 와 Redis 에 저장해두었던 학생회 접속 Id 비교)
    @GetMapping("/list")
    @Operation(summary = "할 일 태그별 조회",
            description = "태그 선택이 없는 경우, 전체 할 일을 조회합니다.",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "todoType", description = "할 일 유형", required = false),
                    @Parameter(name = "progressStatus", description = "진행 상태", required = false)
            }
    )
    public BaseResponse<List<TodoSimpleResponse>> getTodoList(
            @PathVariable String councilName,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) TodoTypeDTO todoType,
            @RequestParam(required = false) ProgressStatus progressStatus
            ) {
        Long councilId = CouncilContextHolder.getCouncilId();
        System.out.println("💬 [GET /list] 요청 도착");
        System.out.println("    🔸 유저 ID: " + userDetails.getId());
        System.out.println("    🔸 학생회 이름: " + councilName);
        System.out.println("    🔸 councilId (from Redis): " + councilId);
        System.out.println("    🔸 todoType: " + todoType);
        System.out.println("    🔸 progressStatus: " + progressStatus);

        List<TodoSimpleResponse> response = todoService.getAllTodoList(
                userDetails.getId(), councilId, todoType, progressStatus);

        System.out.println("    🔹 반환 항목 수: " + response.size());
        return new BaseResponse<>(response);
    }


    @IsCouncil // 접속한 유저가 선택한 학생회 관리지 페이지가 맞는지 (프론트에서 주는 councilId 와 Redis 에 저장해두었던 학생회 접속 Id 비교)
    @PatchMapping("/{todoId}/status")
    @Operation(summary = "할 일 상세 조회 시 상태 변경 API",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)}
    )
    public ResponseEntity<?> updateTodoStatus(
            @PathVariable Long todoId,
            @RequestBody TodoStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String councilName

    ) {
        Long councilId = CouncilContextHolder.getCouncilId();
        todoService.updateTodoStatus(todoId,userDetails.getId(), request.getProgressStatus());
        System.out.println("💬 [patch/status] 요청 도착");
        System.out.println("    🔸 유저 ID: " + userDetails.getId());
        System.out.println("    🔸 학생회 이름: " + councilName);
        System.out.println("    🔸 councilId (from Redis): " + councilId);
        System.out.println("    🔸 변경요청된 progressStatus: " + request.getProgressStatus());

        return ResponseEntity.ok("진행 상태가 변경되었습니다.");
    }

    @IsCouncil // 접속한 유저가 선택한 학생회 관리지 페이지가 맞는지 (프론트에서 주는 councilId 와 Redis 에 저장해두었던 학생회 접속 Id 비교)
    @DeleteMapping("/{todoId}/delete")
    @Operation(summary = "할 일 delete _ 작성자만 가능!",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)}
    )
    @CheckOwner(entity = Todo.class, idParam = "todoId", authorGetter = "getCreateUser.getUserPkId")
    public ResponseEntity<?> deleteTodo(@PathVariable Long todoId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @PathVariable String councilName) {
        Long councilId = CouncilContextHolder.getCouncilId();
        todoService.deleteTodo(todoId, userDetails.getId());
        return ResponseEntity.ok("할일 삭제 완료");
    }

    @IsCouncil // 접속한 유저가 선택한 학생회 관리지 페이지가 맞는지 (프론트에서 주는 councilId 와 Redis 에 저장해두었던 학생회 접속 Id 비교)
    @GetMapping("/dashboard/todo-summary")
    @Operation(summary = "할 일 summary _ 해당 학생회에서 만들어진 할일 기준임.",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)}
    )
    public ResponseEntity<TodoSummaryResponse> summaryTodo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @PathVariable String councilName) {
        Long councilId = CouncilContextHolder.getCouncilId();
        TodoSummaryResponse response = todoService.getTodoSummary(councilId,userDetails.getId());
        return ResponseEntity.ok(response);
    }



}
