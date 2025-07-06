package org.example.wecambackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsCouncil;
import org.example.wecambackend.dto.requestDTO.TodoCreateRequest;
import org.example.wecambackend.dto.responseDTO.TodoResponse;
import org.example.wecambackend.service.admin.TodoService;
import org.springframework.http.HttpStatus;
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

    @IsCouncil // 접속한 유저가 선택한 학생회 관리지 페이지가 맞는지 (프론트에서 주는 councilId 와 Redis 에 저장해두었던 학생회 접속 Id 비교)
    @PostMapping(value = "/{councilId}/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "할일 등록",
            parameters = {
                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<?> createTodo(
            @PathVariable("councilId") Long councilId,
            @PathVariable("councilName") String councilName,
            @RequestPart("request") @Valid TodoCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        todoService.createTodo(councilId,request, files, userDetails.getId());
        return ResponseEntity.ok("할일 등록이 완료되었습니다.");
    }

//
//    @IsCouncil // 접속한 유저가 선택한 학생회 관리지 페이지가 맞는지 (프론트에서 주는 councilId 와 Redis 에 저장해두었던 학생회 접속 Id 비교)
//    @PostMapping(value = "/{councilId}/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(summary = "할일 등록",
//            parameters = {
//                    @Parameter(name = "X-Council-Id", description = "현재 접속한 학생회 ID", in = ParameterIn.HEADER)
//            })
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청")
//    })


}
