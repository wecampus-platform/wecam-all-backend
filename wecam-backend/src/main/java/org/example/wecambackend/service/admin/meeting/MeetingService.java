package org.example.wecambackend.service.admin.meeting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.common.BaseEntity;
import org.example.model.council.Council;
import org.example.model.council.CouncilMember;
import org.example.model.meeting.Meeting;
import org.example.model.meeting.MeetingAttendee;
import org.example.model.meeting.MeetingFile;
import org.example.model.enums.MeetingAttendanceStatus;
import org.example.model.enums.MeetingRole;
import org.example.model.user.User;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.dto.request.meeting.MeetingCreateRequest;
import org.example.wecambackend.dto.response.meeting.MeetingResponse;
import org.example.wecambackend.repos.council.CouncilMemberRepository;
import org.example.wecambackend.repos.council.CouncilRepository;
import org.example.wecambackend.repos.meeting.MeetingAttendeeRepository;
import org.example.wecambackend.repos.meeting.MeetingFileRepository;
import org.example.wecambackend.repos.meeting.MeetingRepository;
import org.example.wecambackend.repos.user.UserRepository;
import org.example.wecambackend.repos.category.CategoryRepository;
import org.example.wecambackend.repos.category.CategoryAssignmentRepository;
import org.example.model.category.Category;
import org.example.model.category.CategoryAssignment;
import org.example.wecambackend.service.client.common.filesave.FileStorageService;
import org.example.wecambackend.service.client.common.filesave.FilePath;
import org.example.wecambackend.util.FileValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingFileRepository meetingFileRepository;
    private final MeetingAttendeeRepository meetingAttendeeRepository;
    private final CouncilMemberRepository councilMemberRepository;
    private final CouncilRepository councilRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryAssignmentRepository categoryAssignmentRepository;
    private final FileStorageService fileStorageService;

    /**
     * 회의록 생성
     */
    @Transactional
    public MeetingResponse createMeeting(MeetingCreateRequest request, Long userId) {
        // CouncilContextHolder에서 현재 학생회 ID 가져오기
        Long councilId = org.example.wecambackend.common.context.CouncilContextHolder.getCouncilId();

        // 1. 사용자 및 학생회 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));

        Council council = councilRepository.findById(councilId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_NOT_FOUND));

        // 2. 사용자가 해당 학생회에 소속되어 있는지 확인
        CouncilMember councilMember = councilMemberRepository
                .findByUserUserPkIdAndCouncilIdAndStatus(userId, councilId, BaseEntity.Status.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_MISMATCH));

        // 3. 회의록 엔티티 생성
        Meeting meeting = Meeting.builder()
                .title(request.getTitle())
                .meetingDateTime(request.getMeetingDateTime())
                .location(request.getLocation())
                .content(request.getContent())
                .council(council)
                .createdBy(councilMember)
                .build();

        Meeting savedMeeting = meetingRepository.save(meeting);

        // 4. 참석자 정보 저장
        if (request.getAttendees() != null && !request.getAttendees().isEmpty()) {
            saveMeetingAttendees(request.getAttendees(), savedMeeting);
        }

        // 5. 카테고리 할당
        if (request.getCategoryId() != null) {
            saveCategoryAssignment(request.getCategoryId(), savedMeeting.getId(), councilId);
        }

        return convertToResponse(savedMeeting);
    }

    /**
     * 회의록에 파일 추가 업로드
     */
    @Transactional
    public MeetingResponse addFilesToMeeting(Long meetingId, List<MultipartFile> files, Long userId) {
        Long councilId = org.example.wecambackend.common.context.CouncilContextHolder.getCouncilId();

        // 사용자 검증 및 권한 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));

        councilMemberRepository
                .findByUserUserPkIdAndCouncilIdAndStatus(userId, councilId, BaseEntity.Status.ACTIVE)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_MISMATCH));

        // 회의록 조회 및 학생회 일치 확인
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MEETING_NOT_FOUND));
        if (!meeting.getCouncil().getId().equals(councilId)) {
            throw new BaseException(BaseResponseStatus.COUNCIL_MISMATCH);
        }

        // 작성자만 업로드 가능 확인
        Long creatorUserId = meeting.getCreatedBy().getUser().getUserPkId();
        if (!creatorUserId.equals(userId)) {
            throw new BaseException(BaseResponseStatus.ONLY_AUTHOR_CAN_MODIFY);
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
     * 참석자 정보 저장
     * TODO 구성원 별로 참석, 불참 또는 지각처리 해서 기록하게 할 수도 있음.
     * TODO 참석자 별 역할 (참석자, 진행자, 기록자)도 기록 가능
     * 지금은 일단 참석 정보만 저장하고, 역할도 다 참석자로 처리하는 중
     */
    private void saveMeetingAttendees(List<MeetingCreateRequest.MeetingAttendeeRequest> attendeeRequests, 
                                    Meeting meeting) {
        List<MeetingAttendee> attendees = new ArrayList<>();
        
        for (MeetingCreateRequest.MeetingAttendeeRequest request : attendeeRequests) {
            CouncilMember member = councilMemberRepository.findById(request.getCouncilMemberId())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_MEMBER_NOT_FOUND));

            MeetingAttendanceStatus status = MeetingAttendanceStatus.PRESENT;
            if (request.getAttendanceStatus() != null) {
                status = request.getAttendanceStatus();
            }

            MeetingRole role = MeetingRole.ATTENDEE;
            if (request.getRole() != null) {
                role = request.getRole();
            }

            MeetingAttendee attendee = MeetingAttendee.builder()
                    .meeting(meeting)
                    .councilMember(member)
                    .attendanceStatus(status)
                    .role(role)
                    .build();

            attendees.add(attendee);
        }

        meetingAttendeeRepository.saveAll(attendees);
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
     * 카테고리 할당 저장
     */
    private void saveCategoryAssignment(Long categoryId, Long meetingId, Long councilId) {
        // 카테고리가 해당 학생회에 속하는지 확인
        Category category = categoryRepository.findByIdAndCouncilId(categoryId, councilId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.CATEGORY_NOT_FOUND));

        // 카테고리 할당 생성
        CategoryAssignment categoryAssignment = CategoryAssignment.create(
                category, 
                CategoryAssignment.EntityType.MEETING, 
                meetingId
        );

        categoryAssignmentRepository.save(categoryAssignment);
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
        List<MeetingFile> files = meetingFileRepository.findByMeetingIdOrderByCreatedAtAsc(meeting.getId());
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
        String categoryName = null;
        Long categoryId = null;
        if (meeting.getId() != null) {
            Optional<CategoryAssignment> categoryAssignment = categoryAssignmentRepository
                    .findByEntityTypeAndEntityId(CategoryAssignment.EntityType.MEETING, meeting.getId());
            if (categoryAssignment.isPresent()) {
                categoryName = categoryAssignment.get().getCategory().getName();
                categoryId = categoryAssignment.get().getCategory().getId();
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
                .categoryId(categoryId)
                .categoryName(categoryName)
                .attendees(attendeeResponses)
                .files(fileResponses)
                .createdAt(meeting.getCreatedAt())
                .updatedAt(meeting.getUpdatedAt())
                .build();
    }
}
