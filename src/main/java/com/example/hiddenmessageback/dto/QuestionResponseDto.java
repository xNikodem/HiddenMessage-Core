package com.example.hiddenmessageback.dto;

import lombok.Data;

@Data
public class QuestionResponseDto {
    private Long questionId;
    private String question;
    private String type;
    private Integer length;
}

