package com.example.demo.Controller;

import com.example.demo.config.VietQRGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.ByteArrayOutputStream;


@RestController
@RequestMapping("/api/qr")
public class QRController {
    private final VietQRGenerator qrService;

    public QRController(VietQRGenerator qrService) {
        this.qrService = qrService;
    }

    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRImage(
            @RequestParam String acquierID,
            @RequestParam String consumerID,
//            @RequestParam String amount,
            @RequestParam(required = false, defaultValue = "QRIBFTTA") String note
    ) throws Exception {
        String qrText = qrService
                .setBeneficiaryOrganization(acquierID, consumerID)
                .setAdditionalDataFieldTemplate(note)
                .build();

        // Sinh ảnh QR từ chuỗi
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitMatrix matrix = new MultiFormatWriter().encode(qrText, BarcodeFormat.QR_CODE, 300, 300);
        MatrixToImageWriter.writeToStream(matrix, "PNG", stream);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(stream.toByteArray());
    }
}
