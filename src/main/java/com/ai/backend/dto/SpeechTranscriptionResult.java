package com.ai.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpeechTranscriptionResult {
    private String transcript;
    private float confidence;
}
