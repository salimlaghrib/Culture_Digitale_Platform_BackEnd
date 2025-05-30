package salimlgh.culturedigitalplatform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import salimlgh.culturedigitalplatform.entities.Course;
import salimlgh.culturedigitalplatform.entities.Formation;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Méthodes de base
    Optional<Course> findByTitle(String title);
    List<Course> findByStatus(String status);
    Page<Course> findByStatus(String status, Pageable pageable);

    // Recherche par formation
    List<Course> findByFormation(Formation formation);
    Page<Course> findByFormation(Formation formation, Pageable pageable);

    // Recherche de cours actifs
    @Query("SELECT c FROM Course c WHERE c.status = 'active'")
    List<Course> findActiveCourses();

    @Query("SELECT c FROM Course c WHERE c.status = 'active'")
    Page<Course> findActiveCourses(Pageable pageable);

    // Recherche de cours inactifs
    @Query("SELECT c FROM Course c WHERE c.status = 'inactive'")
    List<Course> findInactiveCourses();

    // Recherche avec PDF


    // Recherche avec vidéo
    @Query("SELECT c FROM Course c WHERE c.youtubeLink IS NOT NULL AND c.status = 'active'")
    List<Course> findActiveCoursesWithVideo();

    // Recherche textuelle (active seulement)
    @Query("SELECT c FROM Course c WHERE " +
            "c.status = 'active' AND " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Course> searchActiveCourses(@Param("keyword") String keyword);

    // Recherche avancée filtrée
    @Query("SELECT c FROM Course c WHERE " +
            "(:formationId IS NULL OR c.formation.id = :formationId) AND " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> advancedSearch(
            @Param("formationId") Long formationId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Derniers cours actifs
    @Query("SELECT c FROM Course c WHERE c.status = 'active' ORDER BY c.createdAt DESC LIMIT 5")
    List<Course> findLatestActiveCourses();

    // Vérification d'existence
    boolean existsByTitleAndStatus(String title, String status);

    // Statistiques
    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = 'active'")
    long countActiveCourses();

    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = 'inactive'")
    long countInactiveCourses();

}