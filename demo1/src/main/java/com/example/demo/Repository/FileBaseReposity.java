package com.example.demo.Repository;

import com.example.demo.Entities.FileBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileBaseReposity extends JpaRepository<FileBase, Integer> {

}
