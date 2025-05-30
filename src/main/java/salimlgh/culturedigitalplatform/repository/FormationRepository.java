package salimlgh.culturedigitalplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import salimlgh.culturedigitalplatform.entities.DifficultyLevel;
import salimlgh.culturedigitalplatform.entities.Formation;

import java.util.List;
import java.util.Optional;

public interface FormationRepository extends JpaRepository<Formation, Long> {
    Optional<Formation> findByTitle(String title);
    List<Formation> findByTitleContainingOrDescriptionContaining(String title, String description);
    List<Formation> findAllByOrderByCreatedAtDesc();
    List<Formation> findByDifficulty(DifficultyLevel difficulty);
    List<Formation> findByCategory(String category);

    // Queries for published formations
    @Query("SELECT f FROM Formation f WHERE f.published = true")
    List<Formation> findPublishedFormations();

    @Query("SELECT f FROM Formation f WHERE f.id = :id AND f.published = true")
    Optional<Formation> findPublishedById(@Param("id") Long id);

    @Query("SELECT f FROM Formation f WHERE f.published = true AND (f.title LIKE %:keyword% OR f.description LIKE %:keyword%)")
    List<Formation> searchPublishedFormations(@Param("keyword") String keyword);

    @Query("SELECT f FROM Formation f WHERE f.published = true AND f.difficulty = :difficulty")
    List<Formation> findPublishedByDifficulty(@Param("difficulty") DifficultyLevel difficulty);

    @Query("SELECT f FROM Formation f WHERE f.published = true AND f.category = :category")
    List<Formation> findPublishedByCategory(@Param("category") String category);

    @Query("SELECT f FROM Formation f WHERE f.published = true ORDER BY f.createdAt DESC")
    List<Formation> findPublishedFormationsOrderByCreatedAtDesc();
}
