package com.ai.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FaceValidationResult {
    private boolean faceDetected;
    private String blurLikelihood;
    private HeadPose headPose;

    @Data
    @Builder
    public static class HeadPose {
        private float rollAngle;
        private float panAngle;
        private float tiltAngle;
    }
}
