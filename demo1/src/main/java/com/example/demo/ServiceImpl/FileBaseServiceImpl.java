package com.example.demo.ServiceImpl;

import com.example.demo.Entities.FileBase;
import com.example.demo.Repository.FileBaseReposity;
import com.example.demo.Service.FileBaseSerVice;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class FileBaseServiceImpl implements FileBaseSerVice {
    @Autowired
    private FileBaseReposity fileBaseReposity;
    private final Storage storage; // Firebase Storage client
    @Value("${bucket-name}")
    private String bucketName;

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
        String objectName = "uploads/" + fileBase.getFileName();
        BlobId blobId = BlobId.of(bucketName, objectName);
        storage.delete(blobId);
        fileBaseReposity.deleteById(Math.toIntExact(id));
    }
    @Override
    public byte[] downloadFileAsBytes(String filePath) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.get(filePath);

        if (blob == null) {
            throw new IOException("File not found: " + filePath);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blob.downloadTo(outputStream);
        return outputStream.toByteArray();
    }


}
