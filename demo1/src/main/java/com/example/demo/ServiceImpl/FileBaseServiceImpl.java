package com.example.demo.ServiceImpl;

import com.example.demo.Entities.FileBase;
import com.example.demo.Repository.FileBaseReposity;
import com.example.demo.Service.FileBaseSerVice;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FileBaseServiceImpl implements FileBaseSerVice {
    @Autowired
    private FileBaseReposity fileBaseReposity;
    private final Storage storage; // Firebase Storage client
    private final String bucketName = "image-1acd5.appspot.com";

    public FileBaseServiceImpl(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void Save(FileBase fileBase) {
        fileBaseReposity.save(fileBase);
    }

    @Override
    public void deleteFileInDB(Long id) {
        FileBase fileBase = fileBaseReposity.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("File không tồn tại với id: " + id));
            String objectName = "uploads/"+fileBase.getFileName();
            BlobId blobId = BlobId.of(bucketName, objectName);
            storage.delete(blobId);
            System.out.println(blobId);
            storage.delete(blobId);
            fileBaseReposity.deleteById(Math.toIntExact(id));
    }




}
