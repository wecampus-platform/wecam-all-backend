package org.example.wecambackend.service.admin.filelibrary;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.category.Category;
import org.example.model.category.CategoryAssignment;
import org.example.model.common.BaseEntity;
import org.example.model.council.Council;
import org.example.model.file.FileAsset;
import org.example.model.file.FileAssetFinalization;
import org.example.model.file.FinalStatus;
import org.example.model.user.User;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.projection.FileItemDto;
import org.example.wecambackend.dto.projection.FileLibraryFilter;
import org.example.wecambackend.dto.request.FileUploadRequest;
import org.example.wecambackend.dto.response.FileAssetResponse;
import org.example.wecambackend.dto.response.admin.AdminFileResponse;
import org.example.wecambackend.repos.category.CategoryAssignmentRepository;
import org.example.wecambackend.repos.category.CategoryRepository;
import org.example.wecambackend.repos.filelibrary.FileAssetFinalizationRepository;
import org.example.wecambackend.repos.filelibrary.FileAssetRepository;
import org.example.wecambackend.repos.filelibrary.FileLibraryQueryRepository;
import org.example.wecambackend.repos.filelibrary.FileLibraryQueryRepositoryImpl;
import org.example.wecambackend.service.admin.CategoryService;
import org.example.wecambackend.service.admin.Enum.UploadFolder;
import org.example.wecambackend.service.admin.common.AdminFileStorageService;
import org.example.wecambackend.service.admin.common.EntityFinderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileLibraryService {
    private final FileLibraryQueryRepository fileLibraryQueryRepository;
    private final FileAssetFinalizationRepository fileAssetFinalizationRepository;
    private final AdminFileStorageService adminFileStorageService;
    private final CategoryAssignmentRepository categoryAssignmentRepository;
    private final EntityFinderService entityFinderService;
    private final CategoryRepository categoryRepository;
    public Page<FileItemDto> search(FileLibraryFilter filter, Pageable pageable) {
        return fileLibraryQueryRepository.search(filter, pageable);
    }

    @Transactional
    public void upload(Long councilId, Long userId, MultipartFile file, FileUploadRequest req){


        AdminFileResponse adminFileResponses= adminFileStorageService.saveFile(file, UploadFolder.FILE_ASSET); //파일 저장
        Council council = entityFinderService.getCouncilByIdOrThrow(councilId);
        User user = entityFinderService.getUserByIdOrThrow(userId);

        // 2) file_asset 레코드 생성
        FileAsset fa = FileAsset.builder()
                .uuid(UUID.randomUUID())
                .originalFileName(adminFileResponses.getOriginalFileName())
                .storedFileName(adminFileResponses.getStoredFileName())
                .filePath(adminFileResponses.getFilePath())
                .fileUrl(adminFileResponses.getUrl())
                .description(req.description())
                .council(council)
                .user(user)
                .isFinal(req.isFinal())
                .build();

        FileAsset save = fileAssetRepository.save(fa);

        boolean wantFinalize = Boolean.TRUE.equals(req.isFinal());
        boolean wantRequest  = Boolean.TRUE.equals(req.requestFinal());

        if (wantFinalize) {
            // 권한 있으면 즉시 확정, 없으면 요청으로 다운그레이드
            tryFinalizeNow(fa, userId);        // <- 아래 @PreAuthorize 메서드 호출
        } else if (wantRequest) {
            createPendingRequest(fa.getId(), userId, CategoryAssignment.EntityType.FILE_ASSET);
        }

        if (req.categoryIds() != null) {
            saveOrUpdateCategoryAssignments(req.categoryIds(), save.getId(), councilId);
        }



    }


//    @PreAuthorize("@authz.canFinalizeImmediately(authentication)")
    public void tryFinalizeNow(FileAsset fa, Long userId) {
        fa.setFinal(true);
        fa.setFinalStatus(FinalStatus.APPROVED);
        fa.setFinalSetBy(userId);
        fa.setFinalSetAt(LocalDateTime.now());
        fileAssetRepository.save(fa);
        // 즉시확정은 요청 레코드 만들지 않음
    }

    public void createPendingRequest(Long entityId, Long userId, CategoryAssignment.EntityType type) {

        User user = entityFinderService.getUserByIdOrThrow(userId);

        FileAssetFinalization req = FileAssetFinalization.builder()
                .entityType(type)
                .entityId(entityId)
                .finalStatus(FinalStatus.PENDING)
                .requestedBy(user)  // 편의 팩토리 또는 엔티티 로딩
                .requestedAt(LocalDateTime.now())
                .build();
        fileAssetFinalizationRepository.save(req);

        // 본체 상태를 PENDING으로 올려서 목록/상세에서 “승인 대기중” 노출
        fileAssetRepository.findById(entityId).ifPresent(f -> {
            f.setFinalStatus(FinalStatus.PENDING);
            fileAssetRepository.save(f);
        });
    }


    /**
     * 카테고리 할당 여러 개 저장 (기존 할당과 비교하여 다를 때만 INACTIVE로 변경하고 새로운 할당 생성)
     */
    private void saveOrUpdateCategoryAssignments(List<Long> categoryIds, Long fileId, Long councilId) {
        // 1. 기존 모든 ACTIVE 상태의 할당 조회
        List<CategoryAssignment> existingAssignments = categoryAssignmentRepository
                .findAllByEntityTypeAndEntityIdAndStatus(CategoryAssignment.EntityType.FILE_ASSET, fileId, BaseEntity.Status.ACTIVE);

        // 2. 기존 할당의 카테고리 ID 집합
        java.util.Set<Long> existingCategoryIds = existingAssignments.stream()
                .map(assignment -> assignment.getCategory().getId())
                .collect(java.util.stream.Collectors.toSet());

        // 3. 요청된 카테고리 ID 집합
        java.util.Set<Long> requestedCategoryIds = new java.util.HashSet<>(categoryIds);

        // 4. 카테고리 비교: 다를 때만 기존 할당을 삭제하고 새로 생성
        if (!existingCategoryIds.equals(requestedCategoryIds)) {
            log.info("카테고리 변경 감지: 기존 {} -> 요청 {}", existingCategoryIds, requestedCategoryIds);

            // 기존 할당들을 모두 삭제 (Soft Delete가 아닌 Hard Delete)
            if (!existingAssignments.isEmpty()) {
                categoryAssignmentRepository.deleteAll(existingAssignments);
                log.info("기존 카테고리 할당 {}개 삭제 완료", existingAssignments.size());
            }

            // 새로운 할당 생성 (ACTIVE)
            for (Long categoryId : categoryIds) {
                Category category = categoryRepository.findByIdAndCouncilId(categoryId, councilId)
                        .orElseThrow(() -> new BaseException(BaseResponseStatus.CATEGORY_NOT_FOUND));

                CategoryAssignment newAssignment = CategoryAssignment.create(
                        category,
                        CategoryAssignment.EntityType.FILE_ASSET,
                        fileId
                );
                categoryAssignmentRepository.save(newAssignment);

                log.info("새로운 카테고리 할당 생성: 할일 ID {}, 카테고리 ID {}", fileId, categoryId);
            }
        } else {
            log.info("카테고리 변경 없음: 기존과 동일한 카테고리 유지");
        }
    }

    private final FileAssetRepository fileAssetRepository;
}
