package com.zhans.ntsdserver.repository;

import com.zhans.ntsdserver.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByFileName(String filename);
    boolean existsByFileName(String filename);
}
