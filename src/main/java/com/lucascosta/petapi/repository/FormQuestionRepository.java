package com.lucascosta.petapi.repository;

import com.lucascosta.petapi.domain.form.FormQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface FormQuestionRepository extends JpaRepository<FormQuestion, UUID> {
}
