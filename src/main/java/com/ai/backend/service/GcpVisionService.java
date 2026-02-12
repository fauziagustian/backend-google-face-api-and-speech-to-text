package com.ai.backend.service;

import com.ai.backend.dto.FaceValidationResult;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class GcpVisionService {

    public boolean hasFace(MultipartFile file) throws IOException {
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            List<AnnotateImageRequest> requests = prepareRequest(file);
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            
            for (AnnotateImageResponse res : response.getResponsesList()) {
                if (res.hasError()) {
                    log.error("Error detecting face: {}", res.getError().getMessage());
                    return false;
                }
                return !res.getFaceAnnotationsList().isEmpty();
            }
        }
        return false;
    }

    public FaceValidationResult validateFace(MultipartFile file) throws IOException {
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            List<AnnotateImageRequest> requests = prepareRequest(file);
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            
            for (AnnotateImageResponse res : response.getResponsesList()) {
                if (res.hasError()) {
                    throw new RuntimeException("Error detecting face: " + res.getError().getMessage());
                }

                List<FaceAnnotation> faces = res.getFaceAnnotationsList();
                if (faces.isEmpty()) {
                    return FaceValidationResult.builder()
                            .faceDetected(false)
                            .build();
                }

                // Get first face
                FaceAnnotation face = faces.get(0);
                
                boolean isFront = isFrontFacing(face);
                boolean isSide = isSideFacing(face);

                return FaceValidationResult.builder()
                        .faceDetected(true)
                        .blurLikelihood(face.getBlurredLikelihood().name())
                        .isFrontFace(isFront)
                        .isSideFace(isSide)
                        .headPose(FaceValidationResult.HeadPose.builder()
                                .rollAngle(face.getRollAngle())
                                .panAngle(face.getPanAngle())
                                .tiltAngle(face.getTiltAngle())
                                .build())
                        .build();
            }
        }
        return FaceValidationResult.builder().faceDetected(false).build();
    }

    private boolean isFrontFacing(FaceAnnotation face) {
        // Front facing means looking relatively straight at the camera
        // Pan angle (left/right) should be small (e.g., within +/- 15 degrees)
        // Tilt angle (up/down) should be small (e.g., within +/- 15 degrees)
        return Math.abs(face.getPanAngle()) <= 15 && Math.abs(face.getTiltAngle()) <= 15;
    }

    private boolean isSideFacing(FaceAnnotation face) {
        // Side facing means looking significantly to the left or right
        // Pan angle should be greater than a threshold (e.g., > 25 degrees or < -25 degrees)
        return Math.abs(face.getPanAngle()) > 25;
    }

    private List<AnnotateImageRequest> prepareRequest(MultipartFile file) throws IOException {
        ByteString imgBytes = ByteString.copyFrom(file.getBytes());
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
        
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        
        return Collections.singletonList(request);
    }
}
