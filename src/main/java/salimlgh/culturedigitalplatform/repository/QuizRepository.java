package salimlgh.culturedigitalplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import salimlgh.culturedigitalplatform.entities.Quiz;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourseId(Long courseId);

    List<Quiz> findQuizzesByCourseId(Long courseId); // Retourne une liste
    Optional<Quiz> findById(Long id);

    boolean existsByCourseId(Long courseId);
}