package com.example.demo.ServiceImpl;

import com.example.demo.Entities.FileBase;
import com.example.demo.Repository.FileBaseReposity;
import com.example.demo.Service.FileBaseSerVice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FileBaseServiceImpl implements FileBaseSerVice {
    @Autowired
    private FileBaseReposity fileBaseReposity;

    @Override
    public void Save(FileBase fileBase) {
        fileBaseReposity.save(fileBase);
    }
}
