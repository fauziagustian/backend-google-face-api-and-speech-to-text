package com.ai.backend.controller;

import com.ai.backend.dto.ApiResponse;
import com.ai.backend.dto.FaceValidationResult;
import com.ai.backend.service.GcpStorageService;
import com.ai.backend.service.GcpVisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/face")
@RequiredArgsConstructor
public class FaceController {

    private final GcpVisionService visionService;
    private final GcpStorageService storageService;

    @PostMapping(value = "/enroll", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> enrollFace(
            @RequestParam("userId") String userId,
            @RequestPart("frontImage") MultipartFile frontImage,
            @RequestPart("sideImage") MultipartFile sideImage
    ) throws IOException {
        
        // 1. Validate Front Image
        FaceValidationResult frontResult = visionService.validateFace(frontImage);
        if (!frontResult.isFaceDetected()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("No face detected in front image"));
        }
        if (!frontResult.isFrontFace()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid Front Image: Please look straight at the camera. Pan: " + 
                            frontResult.getHeadPose().getPanAngle()));
        }

        // 2. Validate Side Image
        FaceValidationResult sideResult = visionService.validateFace(sideImage);
        if (!sideResult.isFaceDetected()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("No face detected in side image"));
        }
        if (!sideResult.isSideFace()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid Side Image: Please rotate your head to the side (> 25 degrees). Pan: " + 
                            sideResult.getHeadPose().getPanAngle()));
        }

        // 2. Upload to Storage
        String frontPath = "users/" + userId + "/front.jpg";
        String sidePath = "users/" + userId + "/side.jpg";
        
        storageService.uploadFile(frontImage, frontPath);
        storageService.uploadFile(sideImage, sidePath);

        return ResponseEntity.ok(ApiResponse.success("Face enrollment successful"));
    }

    @PostMapping(value = "/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FaceValidationResult>> validateFace(
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        
        FaceValidationResult result = visionService.validateFace(image);
        return ResponseEntity.ok(ApiResponse.success("Validation result", result));
    }
}
