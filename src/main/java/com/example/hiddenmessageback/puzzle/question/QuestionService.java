package com.example.hiddenmessageback.puzzle.question;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    public Question findById(Long id) {
        return questionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Question not found"));
    }
}

