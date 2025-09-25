package com.example.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.FileInputStream;
import java.io.IOException;


@Configuration
public class FirebaseConfig {


    @PostConstruct
    public void init() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("D://Test/image-1acd5-firebase-adminsdk-eqvw5-a3fa3e0e84.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("image-1acd5.appspot.com") // bucket name
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            System.out.println("Firebase initialized!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Bean
    public Storage firebaseStorage() throws IOException {
        // Đường dẫn tới file serviceAccountKey.json tải từ Firebase Console
        FileInputStream serviceAccount =
                new FileInputStream("D://Test/image-1acd5-firebase-adminsdk-eqvw5-a3fa3e0e84.json");

        return StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()
                .getService();
    }




}
