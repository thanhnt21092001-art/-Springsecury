package com.example.demo.Repository;

import com.example.demo.Entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername (String username);
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.enabled = :enable WHERE u.id = :id")
    void updateUserEnabledStatus(@Param("id") Long id, @Param("enable") boolean enable);

}
