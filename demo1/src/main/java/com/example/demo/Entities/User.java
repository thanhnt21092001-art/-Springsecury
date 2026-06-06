package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String password;
    @Column(unique = true)
    private String role;
    @Column(nullable = false)
    private boolean enabled;
    @Column(unique = true)
    private Date date_end ;

    @Column(unique = true)
    private Date create_date;

    @Column (unique = false)
    private String email;

}
