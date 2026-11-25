package com.example.demo.Repository;

import com.example.demo.Entities.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity>
    findTopByEmailOrderByIdDesc(
            String email
    );

}
