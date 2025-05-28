package com.backend.api.service.aws;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public interface IS3Service {

    // Upload a file to S3
    void uploadFile(String keyName, InputStream inputStream, long contentLength, String contentType);

    // Download a file from S3
    InputStream downloadFile(String keyName);

    CompletableFuture<PutObjectResponse> uploadAsync(String key, byte[] data);

    CompletableFuture<byte[]> downloadAsync(String key);

    // preSigned URL 로 파일 다운로드
    HttpResponse<Void> generatePresignedUploadUrl(MultipartFile uploadFile, String userId) throws IOException;

    //
    // preSigned URL 로 파일 다운로드
    byte[] generatePresignedDownloadUrl(String key);

//    URL getPresignedUrlToUpload(S3FileDto key);
//
//    String checkFileUpload(S3FileDto key);
//
//    void deleteFile(S3FileDto keyName);
//
//    URL getPresignedUrlToDownload(S3FileDto key);

}
