package org.example.wecambackend.service.admin.meeting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.category.Category;
import org.example.model.category.CategoryAssignment;
import org.example.model.common.BaseEntity;
import org.example.model.council.Council;
import org.example.model.council.CouncilMember;
import org.example.model.enums.MeetingAttendanceStatus;
import org.example.model.enums.MeetingRole;
import org.example.model.meeting.*;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.request.meeting.MeetingUpsertRequest;
import org.example.wecambackend.dto.response.meeting.MeetingResponse;
import org.example.wecambackend.dto.request.meeting.MeetingListRequest;
import org.example.wecambackend.dto.response.meeting.MeetingListResponse;
import org.example.wecambackend.dto.response.meeting.MeetingTemplateListResponse;
import org.example.wecambackend.dto.response.meeting.MeetingTemplateResponse;
import org.example.wecambackend.repos.category.CategoryAssignmentRepository;
import org.example.wecambackend.repos.category.CategoryRepository;
import org.example.wecambackend.repos.council.CouncilMemberRepository;
import org.example.wecambackend.repos.meeting.*;
import org.example.wecambackend.service.client.common.filesave.FilePath;
import org.example.wecambackend.service.client.common.filesave.FileStorageService;
import org.example.wecambackend.util.FileValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingFileRepository meetingFileRepository;
    private final MeetingAttendeeRepository meetingAttendeeRepository;
    private final CouncilMemberRepository councilMemberRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryAssignmentRepository categoryAssignmentRepository;
    private final FileStorageService fileStorageService;
    private final MeetingTemplateRepository meetingTemplateRepository;

    /**
     * 회의록 생성
     */
    @Transactional
    public MeetingResponse createMeeting(MeetingUpsertRequest request, Long userId) {
        // CouncilContextHolder에서 현재 학생회 ID 가져오기
        Long councilId = org.example.wecambackend.common.context.CouncilContextHolder.getCouncilId();

        // 1. 사용자가 해당 학생회에 소속되어 있는지 확인 (CouncilMember 조회)
        CouncilMember councilMember = councilMemberRepository
                .findByUserUserPkIdAndCouncilIdAndStatus(userId, councilId, BaseEntity.Status.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_MEMBER_NOT_FOUND));

        // 2. 회의록 엔티티 생성 (councilId로 Council 객체 생성)
        Meeting meeting = Meeting.builder()
                .title(request.getTitle())
                .meetingDateTime(request.getMeetingDateTime())
                .location(request.getLocation())
                .content(request.getContent())
                .council(Council.builder().id(councilId).build())
                .createdBy(councilMember)
                .build();

        Meeting savedMeeting = meetingRepository.save(meeting);

        // 3. 참석자 정보 저장
        if (request.getAttendees() != null && !request.getAttendees().isEmpty()) {
            saveOrUpdateMeetingAttendees(request.getAttendees(), savedMeeting);
        }

        // 4. 카테고리 할당
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            saveOrUpdateCategoryAssignments(request.getCategoryIds(), savedMeeting.getId(), councilId);
        }

        log.info("회의록 생성 완료: 회의록 ID {}, 제목: {}, 생성자 ID {}",
                savedMeeting.getId(), savedMeeting.getTitle(), userId);

        return convertToResponse(savedMeeting);
    }

    /**
     * 회의록에 파일 추가 업로드
     */
    @Transactional
    public MeetingResponse addFilesToMeeting(Long meetingId, Long userId, List<MultipartFile> files) {
        Long councilId = org.example.wecambackend.common.context.CouncilContextHolder.getCouncilId();

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MEETING_NOT_FOUND));

        // 작성자 검증
        CouncilMember me = councilMemberRepository
                .findByUserUserPkIdAndCouncilIdAndStatus(userId, councilId, BaseEntity.Status.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_MEMBER_NOT_FOUND));

        // 기존 ACTIVE 상태의 첨부파일 개수 확인
        long existingFileCount = meetingFileRepository.countByMeetingIdAndStatus(meetingId, BaseEntity.Status.ACTIVE);
        long newFileCount = files != null ? files.size() : 0;
        
        // 최대 3개 제한 확인
        if (existingFileCount + newFileCount > 3) {
            log.warn("첨부파일 개수 초과: 기존 {}개 + 신규 {}개 > 최대 3개", existingFileCount, newFileCount);
            throw new BaseException(BaseResponseStatus.FILE_COUNT_EXCEEDED);
        }

        // 파일 검증 및 저장
        if (files != null && !files.isEmpty()) {
            FileValidationUtil.validateAllFiles(files);
            saveMeetingFiles(files, meeting, councilId);
        } else {
            throw new BaseException(BaseResponseStatus.INVALID_FILE_INPUT);
        }

        return convertToResponse(meeting);
    }

    /**
     * 회의록 수정 (파일 제외)
     */
    @Transactional
    public MeetingResponse updateMeeting(Long meetingId, MeetingUpsertRequest request, Long userId) {
        Long councilId = org.example.wecambackend.common.context.CouncilContextHolder.getCouncilId();

        // @CheckCouncilEntity가 이미 검증을 수행함
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MEETING_NOT_FOUND));

        // 작성자 권한 검증
        CouncilMember changedBy = councilMemberRepository
                .findByUserUserPkIdAndCouncilIdAndStatus(userId, councilId, BaseEntity.Status.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_MEMBER_NOT_FOUND));

        // 회의록 작성자와 현재 사용자 비교
        Long meetingCreatorUserId = meeting.getCreatedBy().getUser().getUserPkId();
        if (!meetingCreatorUserId.equals(userId)) {
            throw new BaseException(BaseResponseStatus.ONLY_AUTHOR_CAN_MODIFY);
        }

        if (request.getTitle() != null && !Objects.equals(meeting.getTitle(), request.getTitle())) {
            log.info("회의록 제목 변경: {} -> {}", meeting.getTitle(), request.getTitle());
            meeting.setTitle(request.getTitle());
        }
        
        if (request.getMeetingDateTime() != null && !Objects.equals(meeting.getMeetingDateTime(), request.getMeetingDateTime())) {
            log.info("회의 일시 변경: {} -> {}", meeting.getMeetingDateTime(), request.getMeetingDateTime());
            meeting.setMeetingDateTime(request.getMeetingDateTime());
        }
        
        if (request.getLocation() != null && !Objects.equals(meeting.getLocation(), request.getLocation())) {
            log.info("회의 장소 변경: {} -> {}", meeting.getLocation(), request.getLocation());
            meeting.setLocation(request.getLocation());
        }
        
        if (request.getContent() != null && !Objects.equals(meeting.getContent(), request.getContent())) {
            log.info("회의 내용 변경: 회의록 ID {}", meetingId);
            meeting.setContent(request.getContent());
        }

        // 참석자 업데이트 (필드 전달 시에만)
        if (request.getAttendees() != null) {
            saveOrUpdateMeetingAttendees(request.getAttendees(), meeting);
        }

        // 카테고리 업데이트 (필드 전달 시에만)
        if (request.getCategoryIds() != null) {
            saveOrUpdateCategoryAssignments(request.getCategoryIds(), meeting.getId(), councilId);
        }

        return convertToResponse(meeting);
    }

    /**
     * 참석자 정보 저장 및 수정
     * - 제거: 기존에 있고 요청에는 없는 참석자 → deleteAll
     * - 추가: 요청에는 있고 기존에는 없는 참석자 → saveAll
     * - 변경: 둘 다 존재하지만 상태/역할이 달라진 경우 → 엔티티 값만 변경 (JPA dirty checking)
     */
    private void saveOrUpdateMeetingAttendees(List<MeetingUpsertRequest.MeetingAttendeeRequest> requests,
                                     Meeting meeting) {
        // 기존 참석자 로드
        List<MeetingAttendee> existingAttendees = meetingAttendeeRepository.findByMeetingIdOrderByCreatedAtAsc(meeting.getId());

        // 기존 참석자 Map<memberId, attendee>
        java.util.Map<Long, MeetingAttendee> existingByMemberId = new java.util.HashMap<>();
        for (MeetingAttendee attendee : existingAttendees) {
            Long memberId = attendee.getCouncilMember().getId();
            existingByMemberId.put(memberId, attendee);
        }

        // 요청된 참석자 memberId 집합 및 추가할 목록
        java.util.Set<Long> incomingMemberIds = new java.util.HashSet<>();
        List<MeetingAttendee> toAdd = new ArrayList<>();

        for (MeetingUpsertRequest.MeetingAttendeeRequest req : requests) {
            Long memberId = req.getCouncilMemberId();
            incomingMemberIds.add(memberId);

            MeetingAttendanceStatus newStatus = req.getAttendanceStatus() != null
                    ? req.getAttendanceStatus()
                    : MeetingAttendanceStatus.PRESENT;
            MeetingRole newRole = req.getRole() != null
                    ? req.getRole()
                    : MeetingRole.ATTENDEE;

            MeetingAttendee existing = existingByMemberId.get(memberId);
            if (existing == null) {
                // 추가 대상
                CouncilMember member = councilMemberRepository.findById(memberId)
                        .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_MEMBER_NOT_FOUND));

                MeetingAttendee attendee = MeetingAttendee.builder()
                        .meeting(meeting)
                        .councilMember(member)
                        .attendanceStatus(newStatus)
                        .role(newRole)
                        .build();
                toAdd.add(attendee);
            } else {
                // 변경 대상: 상태/역할이 달라졌다면 값을 갱신 → dirty checking으로 반영됨
                if (existing.getAttendanceStatus() != newStatus) {
                    existing.setAttendanceStatus(newStatus);
                }
                if (existing.getRole() != newRole) {
                    existing.setRole(newRole);
                }
            }
        }

        // 제거 대상: 기존에는 있으나 요청에는 없는 참석자들
        List<MeetingAttendee> toRemove = new ArrayList<>();
        for (MeetingAttendee oldAttendee : existingAttendees) {
            Long memberId = oldAttendee.getCouncilMember().getId();
            if (!incomingMemberIds.contains(memberId)) {
                toRemove.add(oldAttendee);
            }
        }

        if (!toRemove.isEmpty()) {
            meetingAttendeeRepository.deleteAll(toRemove);
        }
        if (!toAdd.isEmpty()) {
            meetingAttendeeRepository.saveAll(toAdd);
        }
    }

    /**
     * 첨부파일 저장
     */
    private void saveMeetingFiles(List<MultipartFile> files, Meeting meeting, Long councilId) {
        List<MeetingFile> meetingFiles = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                MeetingFile meetingFile = uploadAndSaveFile(file, meeting, councilId);
                meetingFiles.add(meetingFile);
            }
        }

        if (!meetingFiles.isEmpty()) {
            meetingFileRepository.saveAll(meetingFiles);
        }
    }

    /**
     * 파일 업로드 및 MeetingFile 엔티티 저장
     */
    private MeetingFile uploadAndSaveFile(MultipartFile file, Meeting meeting, Long councilId) {
        try {
            // UUID 생성
            UUID fileUuid = UUID.randomUUID();
            
            // FileStorageService를 사용하여 파일 저장 (councilId 포함)
            Map<String, String> fileInfo = fileStorageService.save(file, fileUuid, FilePath.MEETINGS, councilId);
            
            // MeetingFile 엔티티 생성
            return MeetingFile.builder()
                    .meeting(meeting)
                    .fileName(file.getOriginalFilename())
                    .filePath(fileInfo.get("filePath"))
                    .fileUrl(fileInfo.get("fileUrl"))
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .build();

        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            throw new BaseException(BaseResponseStatus.FILE_SAVE_FAILED);
        }
    }

    /**
     * 카테고리 할당 여러 개 저장 (기존 할당과 비교하여 다를 때만 INACTIVE로 변경하고 새로운 할당 생성)
     */
    private void saveOrUpdateCategoryAssignments(List<Long> categoryIds, Long meetingId, Long councilId) {
        // 1. 기존 모든 ACTIVE 상태의 할당 조회
        List<CategoryAssignment> existingAssignments = categoryAssignmentRepository
                .findAllByEntityTypeAndEntityIdAndStatus(CategoryAssignment.EntityType.MEETING, meetingId, BaseEntity.Status.ACTIVE);
        
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
                        CategoryAssignment.EntityType.MEETING, 
                        meetingId
                );
                categoryAssignmentRepository.save(newAssignment);
                
                log.info("새로운 카테고리 할당 생성: 회의록 ID {}, 카테고리 ID {}", meetingId, categoryId);
            }
        } else {
            log.info("카테고리 변경 없음: 기존과 동일한 카테고리 유지");
        }
    }

    /**
     * Meeting 엔티티를 Response DTO로 변환
     */
    private MeetingResponse convertToResponse(Meeting meeting) {
        // 참석자 정보 조회
        List<MeetingAttendee> attendees = meetingAttendeeRepository.findByMeetingIdOrderByCreatedAtAsc(meeting.getId());
        List<MeetingResponse.MeetingAttendeeResponse> attendeeResponses = attendees.stream()
                .map(attendee -> MeetingResponse.MeetingAttendeeResponse.builder()
                        .id(attendee.getId())
                        .memberName(attendee.getCouncilMember().getUser().getName())
                        .attendanceStatus(attendee.getAttendanceStatus())
                        .role(attendee.getRole())
                        .build())
                .toList();

        // 첨부파일 정보 조회 
        List<MeetingFile> files = meetingFileRepository.findByMeetingIdAndStatusOrderByCreatedAtAsc(
                meeting.getId(), BaseEntity.Status.ACTIVE);
        List<MeetingResponse.MeetingFileResponse> fileResponses = files.stream()
                .map(file -> MeetingResponse.MeetingFileResponse.builder()
                        .id(file.getId())
                        .fileName(file.getFileName())
                        .fileUrl(file.getFileUrl())
                        .fileSize(file.getFileSize())
                        .fileType(file.getFileType())
                        .build())
                .toList();

        // 카테고리 정보 조회
        List<Long> categoryIds = new ArrayList<>();
        List<String> categoryNames = new ArrayList<>();
        if (meeting.getId() != null) {
            // ACTIVE 상태의 카테고리 할당 모두 조회
            List<CategoryAssignment> categoryAssignments = categoryAssignmentRepository
                    .findAllByEntityTypeAndEntityIdAndStatus(CategoryAssignment.EntityType.MEETING, meeting.getId(), BaseEntity.Status.ACTIVE);
            
            for (CategoryAssignment assignment : categoryAssignments) {
                categoryIds.add(assignment.getCategory().getId());
                categoryNames.add(assignment.getCategory().getName());
            }
        }

        return MeetingResponse.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .meetingDateTime(meeting.getMeetingDateTime())
                .location(meeting.getLocation())
                .content(meeting.getContent())
                .createdById(meeting.getCreatedBy().getId())
                .createdByName(meeting.getCreatedBy().getUser().getName())
                .categoryIds(categoryIds)
                .categoryNames(categoryNames)
                .attendees(attendeeResponses)
                .files(fileResponses)
                .createdAt(meeting.getCreatedAt())
                .updatedAt(meeting.getUpdatedAt())
                .build();
    }

    /**
     * 회의록 목록 조회
     */
    @Transactional(readOnly = true)
    public List<MeetingListResponse> getMeetingList(MeetingListRequest request) {
        Long councilId = org.example.wecambackend.common.context.CouncilContextHolder.getCouncilId();

        List<Meeting> meetings = meetingRepository.findMeetingsWithFilters(
                councilId, 
                request.getCategoryId(), 
                request.getAttendeeId(), 
                request.getSortOrder().name());

        List<MeetingListResponse> response = meetings.stream()
                .map(this::convertToMeetingListResponse)
                .collect(Collectors.toList());
        
        return response;
    }

    /**
     * 회의록 상세 조회
     */
    @Transactional(readOnly = true)
    public MeetingResponse getMeeting(Long meetingId) {
        Optional<Meeting> meeting = meetingRepository.findById(meetingId);

        return convertToResponse(meeting.get());
    }

    /**
     * 회의록 템플릿 목록 조회
     */
    @Transactional(readOnly = true)
    public List<MeetingTemplateListResponse> getTemplateList() {
        Long councilId = org.example.wecambackend.common.context.CouncilContextHolder.getCouncilId();

        List<MeetingTemplate> templates = meetingTemplateRepository.findByCouncilIdOrCommon(councilId);

        return templates.stream()
                .map(this::convertToTemplateListResponse)
                .collect(Collectors.toList());
    }

    /**
     * 회의록 템플릿 상세 조회
     */
    @Transactional(readOnly = true)
    public MeetingTemplateResponse getTemplateDetail(Long templateId) {
        Long councilId = org.example.wecambackend.common.context.CouncilContextHolder.getCouncilId();
        
        MeetingTemplate template = meetingTemplateRepository.findById(templateId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.TEMPLATE_NOT_FOUND));
        
        // 해당 템플릿이 현재 학생회에 속하거나 전체 공통 템플릿인지 확인
        if (template.getCouncil() != null && !template.getCouncil().getId().equals(councilId)) {
            throw new BaseException(BaseResponseStatus.TEMPLATE_ACCESS_DENIED);
        }
        
        return convertToTemplateResponse(template);
    }

    /**
     * Meeting 엔티티를 MeetingListResponse로 변환
     */
    private MeetingListResponse convertToMeetingListResponse(Meeting meeting) {
        // 카테고리 정보 조회
        List<CategoryAssignment> categoryAssignments = categoryAssignmentRepository
                .findAllByEntityTypeAndEntityIdAndStatus(
                        CategoryAssignment.EntityType.MEETING, 
                        meeting.getId(), 
                        BaseEntity.Status.ACTIVE);
        
        List<String> categoryNames = categoryAssignments.stream()
                .map(ca -> ca.getCategory().getName())
                .collect(java.util.stream.Collectors.toList());

        // 작성자 프로필 썸네일 이미지 URL 생성
        String profileThumbnailUrl = null;
        if (meeting.getCreatedBy().getUser().getUserInformation() != null 
            && meeting.getCreatedBy().getUser().getUserInformation().getProfileImagePath() != null) {
            String profilePath = meeting.getCreatedBy().getUser().getUserInformation().getProfileImagePath();
            profileThumbnailUrl = "/uploads/" + profilePath.replaceFirst("PROFILE/", "PROFILE_THUMB/");
        }

        return MeetingListResponse.builder()
                .meetingId(meeting.getId())
                .title(meeting.getTitle())
                .meetingDateTime(meeting.getMeetingDateTime())
                .categoryNames(categoryNames)
                .authorName(meeting.getCreatedBy().getUser().getName())
                .authorId(meeting.getCreatedBy().getUser().getUserPkId())
                .authorProfileThumbnailUrl(profileThumbnailUrl)
                .createdAt(meeting.getCreatedAt())
                .build();
    }

    /**
     * MeetingTemplate 엔티티를 MeetingTemplateListResponse 변환
     */
    private MeetingTemplateListResponse convertToTemplateListResponse(MeetingTemplate template) {
        return MeetingTemplateListResponse.builder()
                .templateId(template.getId())
                .templateName(template.getName())
                .isDefault(template.getIsDefault())
                .build();
    }

    /**
     * MeetingTemplate 엔티티를 MeetingTemplateResponse 변환
     */
    private MeetingTemplateResponse convertToTemplateResponse(MeetingTemplate template) {
        return MeetingTemplateResponse.builder()
                .templateId(template.getId())
                .templateName(template.getName())
                .description(template.getDescription())
                .content(template.getContentTemplate())
                .build();
    }
}
