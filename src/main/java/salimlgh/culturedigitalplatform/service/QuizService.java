package salimlgh.culturedigitalplatform.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import salimlgh.culturedigitalplatform.dtos.QuizDTO;
import salimlgh.culturedigitalplatform.entities.Course;
import salimlgh.culturedigitalplatform.entities.Quiz;
import salimlgh.culturedigitalplatform.mapper.MapperQuiz;
import salimlgh.culturedigitalplatform.repository.CourseRepository;
import salimlgh.culturedigitalplatform.repository.QuizRepository;

@Service
@RequiredArgsConstructor// QuizService.java


public class QuizService {
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final MapperQuiz mapperQuiz;

    // Créer un seul quiz
    public QuizDTO createQuiz(Long courseId, QuizDTO quizDTO) {
        // Check if the course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if a quiz already exists for the course
        if (quizRepository.existsByCourseId(courseId)) {
            throw new IllegalStateException("A quiz already exists for this course");
        }

        // Validate the quiz
        validateQuiz(quizDTO);

        // Map DTO to entity and associate it with the course
        Quiz quiz = mapperQuiz.toEntity(quizDTO);
        quiz.setCourse(course);

        // Save the quiz
        Quiz savedQuiz = quizRepository.save(quiz);
        return mapperQuiz.toDto(savedQuiz);
    }

    // Créer plusieurs quizzes
    public List<QuizDTO> createQuizzes(Long courseId, List<QuizDTO> quizDTOs) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        quizDTOs.forEach(this::validateQuiz);

        List<Quiz> quizzes = quizDTOs.stream()
                .map(mapperQuiz::toEntity)
                .peek(quiz -> quiz.setCourse(course))
                .collect(Collectors.toList());

        List<Quiz> savedQuizzes = quizRepository.saveAll(quizzes);
        return savedQuizzes.stream()
                .map(mapperQuiz::toDto)
                .collect(Collectors.toList());
    }

    // Récupérer tous les quizzes d'un cours
    public List<QuizDTO> getQuizzesByCourseId(Long courseId) {
        List<Quiz> quizzes = quizRepository.findByCourseId(courseId);
        if (quizzes.isEmpty()) {
            throw new RuntimeException("No quizzes found for course");
        }
        return quizzes.stream()
                .map(mapperQuiz::toDto)
                .collect(Collectors.toList());
    }

    // Mettre à jour un quiz
    public QuizDTO updateQuiz(Long quizId, QuizDTO quizDTO) {
        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        validateQuiz(quizDTO);

        existingQuiz.setQuestion(quizDTO.getQuestion());
        existingQuiz.setAnswers(quizDTO.getAnswers());
        existingQuiz.setCorrectAnswerIndex(quizDTO.getCorrectAnswerIndex());

        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        return mapperQuiz.toDto(updatedQuiz);
    }

    // Supprimer un quiz
    public void deleteQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new RuntimeException("Quiz not found");
        }
        quizRepository.deleteById(quizId);
    }

    private void validateQuiz(QuizDTO quizDTO) {
        if (quizDTO.getQuestion() == null || quizDTO.getQuestion().trim().isEmpty()) {
            throw new IllegalArgumentException("Question is required");
        }
        if (quizDTO.getAnswers() == null || quizDTO.getAnswers().size() < 2) {
            throw new IllegalArgumentException("At least two answers are required");
        }
        if (quizDTO.getCorrectAnswerIndex() == null ||
                quizDTO.getCorrectAnswerIndex() < 0 ||
                quizDTO.getCorrectAnswerIndex() >= quizDTO.getAnswers().size()) {
            throw new IllegalArgumentException("Invalid correct answer index");
        }
    }
}