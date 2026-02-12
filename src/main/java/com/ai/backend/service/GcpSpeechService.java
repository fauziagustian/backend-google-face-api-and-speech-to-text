package com.ai.backend.service;

import com.ai.backend.dto.SpeechTranscriptionResult;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class GcpSpeechService {

    public SpeechTranscriptionResult transcribe(MultipartFile file) throws IOException {
        log.info("Starting speech transcription for file: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());

        // --- DEBUG START ---
        try {
            if (file.getSize() > 20) {
                // Peek at first 20 bytes to check header (RIFF, etc)
                log.info("Audio first 20 bytes: {}", ByteString.copyFrom(file.getBytes()).substring(0, 20));
            }
            Files.write(Paths.get("debug.wav"), file.getBytes());
            log.info("Saved debug.wav to project root for verification");
        } catch (Exception e) {
            log.error("Failed to save debug file", e);
        }
        // --- DEBUG END ---
        
        try (SpeechClient speechClient = SpeechClient.create()) {
            ByteString audioBytes = ByteString.copyFrom(file.getBytes());

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    // .setSampleRateHertz(16000) // Optional: If not set, service tries to detect from header if valid WAV
                    .setLanguageCode("id-ID")
                    .setEnableAutomaticPunctuation(true)
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            log.debug("Sending request to Google Cloud Speech-to-Text API...");
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            if (results.isEmpty()) {
                log.warn("No transcription results found for file: {}", file.getOriginalFilename());
                return SpeechTranscriptionResult.builder()
                        .transcript("")
                        .confidence(0.0f)
                        .build();
            }

            // Get the first result (most likely)
            SpeechRecognitionResult result = results.get(0);
            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);

            log.info("Transcription successful. Transcript: '{}', Confidence: {}", 
                    alternative.getTranscript(), alternative.getConfidence());

            return SpeechTranscriptionResult.builder()
                    .transcript(alternative.getTranscript())
                    .confidence(alternative.getConfidence())
                    .build();
        } catch (Exception e) {
            log.error("Error during speech transcription: ", e);
            throw e;
        }
    }
}
