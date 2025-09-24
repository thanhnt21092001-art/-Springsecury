package com.example.demo.Service;

import com.example.demo.Entities.FileBase;
import org.springframework.stereotype.Service;

@Service
public interface FileBaseSerVice {
    void Save(FileBase fileBase);
}
