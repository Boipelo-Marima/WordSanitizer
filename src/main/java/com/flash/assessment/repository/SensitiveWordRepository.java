package com.flash.assessment.repository;

import com.flash.assessment.model.SensitiveWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensitiveWordRepository extends JpaRepository<SensitiveWord, Long> {
    boolean existsByWordIgnoreCase(String word);
}
