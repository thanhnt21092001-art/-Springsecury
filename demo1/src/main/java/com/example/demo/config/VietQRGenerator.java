package com.example.demo.config;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
@Service
public class VietQRGenerator {
    private static final String PAYLOAD_FORMAT_INDICATOR = "000201";
    private static final String POINT_OF_INITIATION_METHOD = "010212";
    private static final String GUID = "0010A000000727";
    private static final String SERVICE_CODE = "0208QRIBFTTA";
    private static final String TRANSACTION_CURRENCY = "5303704";
    private static final String COUNTRY_CODE = "5802VN";

    private String consumerAccountInformation = "";
    private String transactionAmount = "";
    private String additionalDataFieldTemplate = "";

    private String convertLength(int len) {
        return len < 10 ? "0" + len : String.valueOf(len);
    }

    /** ✅ Set số tiền */
    public VietQRGenerator setTransactionAmount(String money) {
        String length = convertLength(money.length());
        this.transactionAmount = "54" + length + money;
        return this;
    }

    /** ✅ Set ngân hàng và tài khoản */
    public VietQRGenerator setBeneficiaryOrganization(String acquierID, String consumerID) {
        String acquierLength = convertLength(acquierID.length());
        String acquier = "00" + acquierLength + acquierID;

        String consumerLength = convertLength(consumerID.length());
        String consumer = "01" + consumerLength + consumerID;

        // Gộp toàn bộ thành Merchant Account Info (ID 38)
        String content = GUID + "01" + convertLength(acquier.length() + consumer.length()) + acquier + consumer + SERVICE_CODE;

        String totalLength = convertLength(content.length());
        this.consumerAccountInformation = "38" + totalLength + content;
        return this;
    }

    /** ✅ Set nội dung chuyển tiền */
    public VietQRGenerator setAdditionalDataFieldTemplate(String content) {
        String contentLength = convertLength(content.length());
        // tổng = 08 + len + content → length = 2 + 2 + content.length()
        String totalLength = convertLength(content.length() + 4);
        this.additionalDataFieldTemplate = "62" + totalLength + "08" + contentLength + content;
        return this;
    }

    /** ✅ Hàm tính CRC16-CCITT */
    private String calcCRC(String input) {
        int polynomial = 0x1021;
        int crc = 0xFFFF;

        byte[] bytes = input.getBytes(StandardCharsets.US_ASCII);

        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ polynomial;
                } else {
                    crc <<= 1;
                }
                crc &= 0xFFFF;
            }
        }

        // Kết quả cuối cùng (HEX, 4 ký tự, viết hoa)
        return String.format("%04X", crc);
    }

    /** ✅ Build ra QR chuẩn VietQR/Napas */
    public String build() {
        String contentQR =
                PAYLOAD_FORMAT_INDICATOR +
                        POINT_OF_INITIATION_METHOD +
                        consumerAccountInformation +
                        TRANSACTION_CURRENCY +
                        transactionAmount +
                        COUNTRY_CODE +
                        additionalDataFieldTemplate +
                        "6304"; // CRC placeholder

        // ✅ Gọi hàm calcCRC (đã trả về String HEX)
        String crcHex = calcCRC(contentQR);

        // ✅ Ghép lại thành mã QR hoàn chỉnh
        return contentQR + crcHex;
    }
}
