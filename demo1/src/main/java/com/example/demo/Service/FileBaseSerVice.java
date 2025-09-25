package com.example.demo.Service;

import com.example.demo.Entities.FileBase;
import com.google.cloud.storage.Storage;
import org.springframework.stereotype.Service;

@Service
public interface FileBaseSerVice {
    void Save(FileBase fileBase);
     void deleteFileInDB(Long id);
}
