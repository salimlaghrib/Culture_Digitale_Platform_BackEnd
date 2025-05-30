package salimlgh.culturedigitalplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import salimlgh.culturedigitalplatform.entities.DifficultyLevel;
import salimlgh.culturedigitalplatform.entities.Formation;
import salimlgh.culturedigitalplatform.entities.Course;

import salimlgh.culturedigitalplatform.repository.FormationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FormationService {
    private final FormationRepository formationRepository;

    // Get all formations ordered by creation date or other relevant field
    public List<Formation> getAllFormations() {
        return formationRepository.findAllByOrderByCreatedAtDesc();
    }

    // Get formation by ID
    public Formation getFormationById(Long id) {
        return formationRepository.findById(id)
                .orElseThrow(() -> new ExpressionException("Formation not found with id: " + id));
    }

    // Get formation by title
    public Formation getFormationByTitle(String title) {
        return formationRepository.findByTitle(title)
                .orElseThrow(() -> new ExpressionException("Formation not found with title: " + title));
    }

    // Search formations by keyword
    public List<Formation> searchFormations(String keyword) {
        return formationRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword);
    }

    @Transactional
    public Formation createFormation(Formation formation) {
        return formationRepository.save(formation);
    }

    @Transactional
    public Formation updateFormation(Long id, Formation formationDetails) {
        Formation formation = getFormationById(id);

        formation.setTitle(formationDetails.getTitle());
        formation.setDescription(formationDetails.getDescription());
        formation.setSubtitle(formationDetails.getSubtitle());
        formation.setImageUrl(formationDetails.getImageUrl());
        formation.setDifficulty(formationDetails.getDifficulty());
        formation.setCategory(formationDetails.getCategory());
        formation.setDurationHours(formationDetails.getDurationHours());

        return formationRepository.save(formation);
    }

    @Transactional
    public void deleteFormation(Long id) {
        Formation formation = getFormationById(id);
        formationRepository.delete(formation);
    }

    // Get courses for a formation
    public List<Course> getCoursesForFormation(Long formationId) {
        Formation formation = getFormationById(formationId);
        return formation.getCourses();
    }

    // Admin methods
    public long getFormationCount() {
        return formationRepository.count();
    }

    @Transactional
    public Formation publishFormation(Long id, boolean published) {
        Formation formation = getFormationById(id);
        formation.setPublished(published);
        return formationRepository.save(formation);
    }

    // Additional business methods
    public List<Formation> getFormationsByDifficulty(String difficulty) {
        return formationRepository.findByDifficulty(DifficultyLevel.valueOf(difficulty));
    }

    public List<Formation> getFormationsByCategory(String category) {
        return formationRepository.findByCategory(category);
    }

    public List<Formation> findAll() {
        return formationRepository.findAll();
    }

    // Methods for published formations
    public List<Formation> getPublishedFormations() {
        return formationRepository.findPublishedFormations();
    }

    public Formation getPublishedFormationById(Long id) {
        return formationRepository.findPublishedById(id)
                .orElseThrow(() -> new ExpressionException("Published formation not found with id: " + id));
    }

    public List<Formation> searchPublishedFormations(String keyword) {
        return formationRepository.searchPublishedFormations(keyword);
    }

    public List<Formation> getPublishedFormationsByDifficulty(String difficulty) {
        return formationRepository.findPublishedByDifficulty(DifficultyLevel.valueOf(difficulty));
    }

    public List<Formation> getPublishedFormationsByCategory(String category) {
        return formationRepository.findPublishedByCategory(category);
    }

    public List<Formation> getPublishedFormationsOrderByCreatedAtDesc() {
        return formationRepository.findPublishedFormationsOrderByCreatedAtDesc();
    }
}
