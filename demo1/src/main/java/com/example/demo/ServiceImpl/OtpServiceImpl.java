package com.example.demo.ServiceImpl;

import com.example.demo.Entities.OtpEntity;
import com.example.demo.Repository.OtpRepository;
import com.example.demo.Service.OtpService;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OtpServiceImpl implements OtpService {
    @Autowired
    private OtpRepository otpRepository;

    @Transactional
    public void verifyOtp(
            String email,
            String otp
    ) {
        OtpEntity otpEntity =
                otpRepository
                        .findTopByEmailOrderByIdDesc(
                                email
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy OTP"
                                ));
        System.out.println(otpEntity.getOtp());
        // check đã dùng chưa
        if (Boolean.TRUE.equals(
                otpEntity.getUsed()
        )) {
            if (!otpEntity.getOtp()
                    .equals(otp)) {

                throw new RuntimeException(
                        "OTP không đúng"
                );
            }
            throw new RuntimeException(
                    "OTP đã được sử dụng"
            );
        }

        // check hết hạn
        if (LocalDateTime.now().isAfter(
                otpEntity.getExpiredAt()
        )) {

            throw new RuntimeException(
                    "OTP đã hết hạn"
            );
        }

        // check otp đúng không
        if (!otpEntity.getOtp()
                .equals(otp)) {

            throw new RuntimeException(
                    "OTP không đúng"
            );
        }
        // đánh dấu đã dùng
        otpEntity.setUsed(true);
        otpRepository.save(
                otpEntity
        );
    }
}
