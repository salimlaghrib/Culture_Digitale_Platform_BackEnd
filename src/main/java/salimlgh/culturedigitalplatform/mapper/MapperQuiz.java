package salimlgh.culturedigitalplatform.mapper;

import org.springframework.stereotype.Component;

import salimlgh.culturedigitalplatform.dtos.QuizDTO;
import salimlgh.culturedigitalplatform.entities.Quiz;

@Component
public class MapperQuiz {
    public QuizDTO toDto(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setQuestion(quiz.getQuestion());
        dto.setAnswers(quiz.getAnswers());
        dto.setCorrectAnswerIndex(quiz.getCorrectAnswerIndex());
        if (quiz.getCourse() != null) {
            dto.setCourseId(quiz.getCourse().getId());
        }
        return dto;
    }

    public Quiz toEntity(QuizDTO dto) {
        Quiz quiz = new Quiz();
        quiz.setId(dto.getId());
        quiz.setQuestion(dto.getQuestion());
        quiz.setAnswers(dto.getAnswers());
        quiz.setCorrectAnswerIndex(dto.getCorrectAnswerIndex());
        return quiz;
    }
}