package org.example.wecambackend.service.client.Affiliation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OcrService {

    private final RestTemplate restTemplate;

    @Value("${ocr.api.url}") // application.properties에 설정
    private String ocrApiUrl;



    public Map<String, String> requestOcr(MultipartFile imageFile) {
        File tempFile = null;
        try {
            // 메모리 기반 임시 파일 생성
            tempFile = File.createTempFile("ocr-", ".jpg");
            org.apache.commons.io.FileUtils.writeByteArrayToFile(tempFile, imageFile.getBytes());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            //userName , schoolName , orgName , enrollYear , schoolGrade 넘어오게 해놨음.
            ResponseEntity<Map> response = restTemplate.exchange(
                    ocrApiUrl, HttpMethod.POST, requestEntity, Map.class
            );

            return response.getBody();

        } catch (IOException e) {
            throw new RuntimeException("OCR 요청 중 파일 처리 실패", e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete(); // OCR 임시용 파일 삭제
            }
        }
    }
}
