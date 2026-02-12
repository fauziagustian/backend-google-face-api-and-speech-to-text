package com.ai.backend.service;

import com.ai.backend.config.GcpConfig;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GcpStorageService {

    private final GcpConfig gcpConfig;
    private final Storage storage = StorageOptions.newBuilder()
            .setProjectId("ai-project-487201")
            .build()
            .getService();

    public void uploadFile(MultipartFile file, String fileName) throws IOException {
        String bucketName = gcpConfig.getBucketName();
        log.info("Uploading file {} to bucket {}", fileName, bucketName);
        
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
        
        storage.create(blobInfo, file.getBytes());
        log.info("File uploaded successfully");
    }
}
