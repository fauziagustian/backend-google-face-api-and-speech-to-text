package com.ai.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gcp")
@Data
public class GcpConfig {
    private String bucketName;

    @PostConstruct
    public void testCredential() throws Exception {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        System.out.println("Credential class: " + credentials.getClass().getName());
        System.out.println("Project ID: " + System.getenv("GOOGLE_CLOUD_PROJECT"));
        
        // Tambahan untuk debugging lebih detail jika menggunakan Service Account JSON
        if (credentials instanceof com.google.auth.oauth2.ServiceAccountCredentials) {
            System.out.println("Project ID (from JSON): " + ((com.google.auth.oauth2.ServiceAccountCredentials) credentials).getProjectId());
        }
    }
}
