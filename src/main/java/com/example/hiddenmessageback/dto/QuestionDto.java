package com.example.hiddenmessageback.dto;

import lombok.Data;

@Data
public class QuestionDto {
    private String question;
    private String answer;
    private String type;
    private Integer length;

}
