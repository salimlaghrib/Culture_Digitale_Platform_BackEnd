package salimlgh.culturedigitalplatform.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import salimlgh.culturedigitalplatform.dtos.QuizDTO;
import salimlgh.culturedigitalplatform.service.QuizService;

import java.util.List;
// QuizController.java

@RestController
@RequestMapping("/api/admin/quizzes")
@RequiredArgsConstructor
public class AdminQuizController {
    private final QuizService quizService;

    // Créer un seul quiz
    @PostMapping("/courses/{courseId}")
    public ResponseEntity<QuizDTO> createQuiz(
            @PathVariable Long courseId,
            @Valid @RequestBody QuizDTO quizDTO) {
        QuizDTO createdQuiz = quizService.createQuiz(courseId, quizDTO);
        return ResponseEntity.ok(createdQuiz);
    }

    // Créer plusieurs quizzes
    @PostMapping("/courses/{courseId}/batch")
    public ResponseEntity<List<QuizDTO>> createQuizzes(
            @PathVariable Long courseId,
            @Valid @RequestBody List<QuizDTO> quizDTOs) {
        List<QuizDTO> createdQuizzes = quizService.createQuizzes(courseId, quizDTOs);
        return ResponseEntity.ok(createdQuizzes);
    }

    // Récupérer tous les quizzes d'un cours
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCourseId(
            @PathVariable Long courseId) {
        List<QuizDTO> quizzes = quizService.getQuizzesByCourseId(courseId);
        return ResponseEntity.ok(quizzes);
    }

    // Mettre à jour un quiz
    @PutMapping("/{quizId}")
    public ResponseEntity<QuizDTO> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizDTO quizDTO) {
        QuizDTO updatedQuiz = quizService.updateQuiz(quizId, quizDTO);
        return ResponseEntity.ok(updatedQuiz);
    }

    // Supprimer un quiz
    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }
}