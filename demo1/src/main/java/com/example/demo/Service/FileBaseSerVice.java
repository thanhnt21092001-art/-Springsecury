package com.example.demo.Service;

import com.example.demo.Entities.FileBase;
import com.google.cloud.storage.Storage;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface FileBaseSerVice {
    void Save(FileBase fileBase);
    void deleteFileInDB(Long id);
    byte[] downloadFileAsBytes(String filePath) throws IOException;
}
