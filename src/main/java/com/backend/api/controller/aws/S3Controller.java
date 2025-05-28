package com.backend.api.controller.aws;

import com.backend.api.common.object.RequestModel;
import com.backend.api.common.utils.S3ObjectKeyValidator;
import com.backend.api.common.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.backend.api.service.aws.IS3Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aws/s3")
public class S3Controller {

    private final IS3Service s3Service;
    private final Utils utils;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) throws IOException {

        RequestModel requestModel = utils.getAttributeRequestModel(request);

        log.info("RequestModel: {}", requestModel);
        String userId = requestModel.getUserId();

        s3Service.uploadFile(file.getOriginalFilename(), file.getInputStream(), file.getSize(), file.getContentType());
        return ResponseEntity.ok("uploaded");
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        InputStream is = s3Service.downloadFile(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(new InputStreamResource(is));
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) throws IOException {

        RequestModel requestModel = utils.getAttributeRequestModel(request);

        // 1. 파일명으로 S3 Key 생성
        String keyName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // 2. S3 Key 유효성 검사
        if (!S3ObjectKeyValidator.isValidS3Key(keyName)) {
            return ResponseEntity.badRequest().body("유효하지 않은 파일명입니다.");
        }

        // 3. Presigned URL 생성 및 S3 객체 업로드
        HttpResponse<Void> response = s3Service.generatePresignedUploadUrl(file, requestModel.getUserId());

        if (response == null || response.statusCode() != 200) {
            assert response != null;
            return ResponseEntity.status(response.statusCode())
                    .body("파일 업로드 실패: " + response.body());
        }

        log.info("파일 업로드 성공: {}", file.getOriginalFilename());
        return ResponseEntity.ok("uploaded");
    }

    @GetMapping("/downloadFile/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        try {
            // 1. Presigned URL 생성 및 S3 객체 다운로드
            byte[] fileBytes = s3Service.generatePresignedDownloadUrl(filename);

            // 2. 파일 이름 추출 및 헤더 설정
            String fileName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileBytes);

        } catch (Exception e) {
            // 에러 로깅 및 500 응답
            return ResponseEntity.status(500)
                    .body(("파일 다운로드 실패: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }
}