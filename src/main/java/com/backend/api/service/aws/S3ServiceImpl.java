package com.backend.api.service.aws;

import com.backend.api.common.utils.MessageLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class S3ServiceImpl implements IS3Service {

    private final S3Client s3Client;
    private final S3AsyncClient s3AsyncClient;
    private final S3Presigner s3Presigner;

    private static final MessageLogger logger = new MessageLogger(LoggerFactory.getLogger(S3ServiceImpl.class));

    private final Environment env;

    @Override
    public void uploadFile(String keyName, InputStream inputStream, long contentLength, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(env.getProperty("aws.s3.bucket"))
                .key(keyName)
                .contentType(contentType)
                .grantFullControl("uri=http://acs.amazonaws.com/groups/global/AllUsers")
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
    }

    @Override
    public InputStream downloadFile(String keyName) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(env.getProperty("aws.s3.bucket"))
                .key(keyName)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
        return response; // 직접 반환하거나 복사해서 사용
    }

    // Asynchronous upload method
    @Override
    public CompletableFuture<PutObjectResponse> uploadAsync(String key, byte[] data) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(env.getProperty("aws.s3.bucket"))
                .key(key)
                .contentType("application/octet-stream")
                .build();

        return s3AsyncClient.putObject(request, AsyncRequestBody.fromBytes(data));
    }

    // Asynchronous download method
    @Override
    public CompletableFuture<byte[]> downloadAsync(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(env.getProperty("aws.s3.bucket"))
                .key(key)
                .build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return s3AsyncClient.getObject(request, AsyncResponseTransformer.toBytes())
                .thenApply(response -> response.asByteArray());
    }

    @Override
    public HttpResponse<Void> generatePresignedUploadUrl(MultipartFile file, String userId) throws IOException {

        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

        File uploadFile = File.createTempFile("upload-", UUID.randomUUID() + "-" + file.getOriginalFilename());
        file.transferTo(uploadFile); // MultipartFile → 실제 임시 파일로 저장

        Map<String, String> metadata = Map.of(
                "uploadedby", userId,
                "originalname", uploadFile.getName()
        );

        String postUrl = createPresignedPostUrl(env.getProperty("aws.s3.bucket"), key, metadata);

//        return useHttpClientToPut(postUrl, new File(key), metadata);
        return useHttpClientToPut(postUrl, uploadFile, metadata);
    }

    @Override
    public byte[] generatePresignedDownloadUrl(String key) {
        String preSignedUrl = createPresignedGetUrl(env.getProperty("aws.s3.bucket"), key);

        return useHttpClientToGet(preSignedUrl);
    }

//    @Override
//    public URL getPresignedUrlToUpload(S3FileDto key) {
//
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(env.getProperty("aws.s3.bucket"))
//                .key(key.getKey())
//                .contentType(key.getContentType())
//                .build();
//
//        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofMinutes(5))
//                .putObjectRequest(putObjectRequest)
//                .build();
//
//        return s3Presigner.presignPutObject(presignRequest).url();
//    }
//
//    @Override
//    public String checkFileUpload(S3FileDto key) {
//        HeadObjectRequest request = HeadObjectRequest.builder()
//                .bucket(env.getProperty("aws.s3.bucket"))
//                .key(key.getKey())
//                .build();
//
//        s3Client.headObject(request);
//        return String.format("https://%s.s3.amazonaws.com/%s", env.getProperty("aws.s3.bucket"), key.getKey());
//    }
//
//    @Override
//    public void deleteFile(S3FileDto key) {
//        DeleteObjectRequest request = DeleteObjectRequest.builder()
//                .bucket(env.getProperty("aws.s3.bucket"))
//                .key(key.getKey())
//                .build();
//
//        s3Client.deleteObject(request);
//    }
//
//    @Override
//    public URL getPresignedUrlToDownload(S3FileDto key) {
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(env.getProperty("aws.s3.bucket"))
//                .key(key.getKey())
//                .build();
//
//        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofMinutes(5))
//                .getObjectRequest(getObjectRequest)
//                .build();
//
//        return s3Presigner.presignGetObject(presignRequest).url();
//    }

    /* Create a pre-signed URL to download an object in a subsequent GET request. */
    public String createPresignedGetUrl(String bucketName, String keyName) {

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1))  // The URL will expire in 10 minutes.
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        logger.infoLog("Presigned URL: [{}]", presignedRequest.url().toString());
        logger.infoLog("HTTP method: [{}]", presignedRequest.httpRequest().method());

        return presignedRequest.url().toExternalForm();

    }

    /* Use the JDK HttpClient (since v11) class to do the download. */
    public byte[] useHttpClientToGet(String presignedUrlString) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // Capture the response body to a byte array.

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            URL presignedUrl = new URL(presignedUrlString);
            HttpResponse<InputStream> response = httpClient.send(requestBuilder
                            .uri(presignedUrl.toURI())
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofInputStream());

            IoUtils.copy(response.body(), byteArrayOutputStream);

            logger.infoLog("HTTP response code is " + response.statusCode());

        } catch (URISyntaxException | InterruptedException | IOException e) {
            logger.errorLog(e.getMessage(), e);
        }
        return byteArrayOutputStream.toByteArray();
    }


    public String createPresignedPostUrl(String bucketName, String keyName, Map<String, String> metadata) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .metadata(metadata)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1))  // The URL expires in 10 minutes.
                .putObjectRequest(objectRequest)
                .build();


        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String myURL = presignedRequest.url().toString();
        logger.infoLog("Presigned URL to upload a file to: [{}]", myURL);
        logger.infoLog("HTTP method: [{}]", presignedRequest.httpRequest().method());

        return presignedRequest.url().toExternalForm();

    }

    /* Use the JDK HttpClient (since v11) class to do the upload. */
    public HttpResponse<Void> useHttpClientToPut(String presignedUrlString, File fileToPut, Map<String, String> metadata) {
        logger.infoLog("Begin [{}] upload", fileToPut.toString());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        metadata.forEach((k, v) -> requestBuilder.header("x-amz-meta-" + k, v));

        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            if (fileToPut.exists()) {
                final HttpResponse<Void> response = httpClient.send(requestBuilder
                                .uri(new URL(presignedUrlString).toURI())
                                .PUT(HttpRequest.BodyPublishers.ofFile(Path.of(fileToPut.toURI())))
                                .build(),
                        HttpResponse.BodyHandlers.discarding());

                logger.infoLog("HTTP response code is " + response.statusCode());

                return response;
            } else {
                logger.errorLog("File does not exist: " + fileToPut);
                return null;
            }

        } catch (URISyntaxException | InterruptedException | IOException e) {
            logger.errorLog(e.getMessage(), e);
            return null;
        }
    }

}
