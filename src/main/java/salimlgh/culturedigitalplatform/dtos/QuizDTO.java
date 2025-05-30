package salimlgh.culturedigitalplatform.dtos;

import java.util.List;

import lombok.Data;

@Data
public class QuizDTO {
    private Long id;
    private String question;
    private List<String> answers;
    private Integer correctAnswerIndex;
    private Long courseId;
} 