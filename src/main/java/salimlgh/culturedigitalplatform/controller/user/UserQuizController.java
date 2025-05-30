package salimlgh.culturedigitalplatform.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import salimlgh.culturedigitalplatform.dtos.QuizDTO;
import salimlgh.culturedigitalplatform.service.QuizService;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class UserQuizController {
    private final QuizService quizService;

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<QuizDTO> getQuizByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok((QuizDTO) quizService.getQuizzesByCourseId(courseId));
    }
} 