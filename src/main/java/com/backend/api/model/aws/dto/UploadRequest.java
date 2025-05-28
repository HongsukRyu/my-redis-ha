package com.backend.api.model.aws.dto;

import com.backend.api.service.aws.IValidS3Key;
import lombok.Data;

@Data
public class UploadRequest {

    @IValidS3Key
    private String s3Key;

    private String filename;
}