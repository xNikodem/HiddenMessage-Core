package com.example.hiddenmessageback.puzzle;

import com.example.hiddenmessageback.puzzle.question.Question;
import com.example.hiddenmessageback.puzzle.question.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PuzzleService {

    private final PuzzleRepository puzzleRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public PuzzleService(PuzzleRepository puzzleRepository, QuestionRepository questionRepository) {
        this.puzzleRepository = puzzleRepository;
        this.questionRepository = questionRepository;
    }

    public Puzzle findByUniqueUrl(String uniqueUrl) {
        return puzzleRepository.findByUniqueUrl(uniqueUrl);
    }

    public void savePuzzleWithQuestions(Puzzle puzzle) {
        Puzzle savedPuzzle = puzzleRepository.save(puzzle);
        for(Question question : puzzle.getQuestions()) {
            question.setPuzzle(savedPuzzle);
            questionRepository.save(question);
        }
    }

}


