package com.example.hiddenmessageback.puzzle;

import com.example.hiddenmessageback.puzzle.question.QuestionDto;
import lombok.Data;

import java.util.List;

@Data
public class PuzzleDto {
    private List<QuestionDto> questions;
    private String message;
}

