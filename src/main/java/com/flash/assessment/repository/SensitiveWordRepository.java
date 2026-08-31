package com.flash.assessment.repository;

import com.flash.assessment.model.SensitiveWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensitiveWordRepository extends JpaRepository<SensitiveWord, Long> {
    List<SensitiveWord> findAll();
}
