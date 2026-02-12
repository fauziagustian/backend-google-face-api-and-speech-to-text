package com.ai.backend.controller;

import com.ai.backend.dto.ApiResponse;
import com.ai.backend.dto.SpeechTranscriptionResult;
import com.ai.backend.service.GcpSpeechService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/speech")
@RequiredArgsConstructor
public class SpeechController {

    private final GcpSpeechService speechService;

    @PostMapping(value = "/stt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SpeechTranscriptionResult>> transcribe(
            @RequestPart("audio") MultipartFile audio
    ) throws IOException {
        
        SpeechTranscriptionResult result = speechService.transcribe(audio);
        return ResponseEntity.ok(ApiResponse.success("Transcription successful", result));
    }
}
