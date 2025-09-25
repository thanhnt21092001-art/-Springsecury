package com.example.demo.Controller;

import com.example.demo.Entities.FileBase;
import com.example.demo.Service.FileBaseSerVice;
import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/files")
public class UploadFileController {
    @Autowired
    private FileBaseSerVice fileBaseSerVice;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
            // Upload file vào Firebase Storage
            Blob blob = StorageClient.getInstance().bucket().create(
                    "uploads/" + file.getOriginalFilename(),
                    file.getInputStream(),
                    file.getContentType()
            );
            FileBase fileBase = new FileBase();
            URL url = StorageClient.getInstance().bucket().getStorage()
                    .signUrl(blob, 7, TimeUnit.DAYS);
            fileBase.setFilePath(url.toString());
            fileBase.setFileName(file.getOriginalFilename());
            fileBase.setFileType(file.getContentType());
            fileBase.setFileSize(String.valueOf(file.getSize()));
            fileBase.setDateUpload(new Date());
            fileBaseSerVice.Save(fileBase);
            Map<String, Object> map = new HashMap<>();
            map.put("Code", HttpServletResponse.SC_OK);
            map.put("Message", "Tải tài liệu thành công");
            return ResponseEntity.ok(map);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        Map<String, Object> map = new HashMap<>();
        fileBaseSerVice.deleteFileInDB(id);
        map.put("Code", HttpServletResponse.SC_OK);
        map.put("message", "Delete file successfully");
        return ResponseEntity.ok(map);

    }


}
