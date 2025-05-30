package salimlgh.culturedigitalplatform.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import salimlgh.culturedigitalplatform.dtos.CourseDTO;
import salimlgh.culturedigitalplatform.entities.Course;
import salimlgh.culturedigitalplatform.entities.Formation;
import salimlgh.culturedigitalplatform.mapper.MapperCourse;
import salimlgh.culturedigitalplatform.repository.CourseRepository;
import salimlgh.culturedigitalplatform.repository.FormationRepository;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final FormationRepository formationRepository;
    private final MapperCourse courseMapper;

    @Transactional
    public CourseDTO createCourse(@Valid CourseDTO courseDTO) throws Exception {
        // Validation des champs obligatoires
        if (courseDTO.getTitle() == null || courseDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }

        if (courseDTO.getDescription() == null || courseDTO.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La description est obligatoire");
        }

        // Validation des doublons
        if (courseRepository.existsByTitleAndStatus(courseDTO.getTitle(), "active")) {
            throw new Exception("Un cours actif avec ce titre existe déjà");
        }

        // Validation de la durée
        if (courseDTO.getDuration() == null || courseDTO.getDuration() < 1) {
            throw new IllegalArgumentException("La durée doit être d'au moins 1 minute");
        }

        // Conversion et création
        Course course = courseMapper.toEntity(courseDTO);
        course.setStatus("active"); // Statut par défaut
        course.setCreatedAt(LocalDateTime.now());

        // Gestion relationnelle
        if (courseDTO.getFormationId() != null) {
            Formation formation = formationRepository.findById(courseDTO.getFormationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Formation not found"));
            course.setFormation(formation);
        }

        // Gestion du PDF
        if (courseDTO.getPdfFile() != null && !courseDTO.getPdfFile().isEmpty()) {
            validatePdfFile(courseDTO.getPdfFile());
            course.setPdfContent(courseDTO.getPdfFile().getBytes());
            course.setPdfContentType(courseDTO.getPdfFile().getContentType());
        }

        // Gestion YouTube
        if (courseDTO.getYoutubeLink() != null) {
            course.setYoutubeLink(validateAndExtractYoutubeId(courseDTO.getYoutubeLink()));
        }

        Course savedCourse = courseRepository.save(course);
        return enrichWithPdfUrl(courseMapper.toDto(savedCourse));
    }

    // Méthodes de validation supplémentaires
    private void validatePdfFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier PDF ne peut pas être vide");
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("Seuls les fichiers PDF sont acceptés");
        }
        if (file.getSize() > 10_000_000) { // 10MB max
            throw new IllegalArgumentException("La taille du PDF ne doit pas dépasser 10MB");
        }
    }

    private String validateAndExtractYoutubeId(String url) {
        // Implémentez la logique de validation et d'extraction de l'ID YouTube
        if (!url.matches("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be)/.+")) {
            throw new IllegalArgumentException("URL YouTube invalide");
        }
        return extractYoutubeId(url); // Votre méthode existante
    }

    public List<CourseDTO> getAllActiveCourses() {
        return courseRepository.findActiveCourses().stream()
                .map(this::convertToDto)
                .toList();
    }

    public Page<CourseDTO> getAllActiveCourses(Pageable pageable) {
        return courseRepository.findActiveCourses(pageable)
                .map(this::convertToDto);
    }

    public CourseDTO getActiveCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (!"active".equals(course.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active course not found");
        }

        return convertToDto(course);
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) throws IOException {
        Course existingCourse = getExistingCourse(id);

        updateCourseFields(existingCourse, courseDTO);
        setFormationIfProvided(courseDTO, existingCourse);
        updatePdfIfProvided(courseDTO, existingCourse);
        processYoutubeLink(courseDTO, existingCourse);

        Course updatedCourse = courseRepository.save(existingCourse);
        return enrichWithPdfUrl(courseMapper.toDto(updatedCourse));
    }

    @Transactional
    public void deactivateCourse(Long id) {
        Course course = getExistingCourse(id);
        course.setStatus("inactive");
        courseRepository.save(course);
    }

    @Transactional
    public void activateCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        course.setStatus("active");
        courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        // Supprimer explicitement les quizzes liés
        if (course.getQuizzes() != null && !course.getQuizzes().isEmpty()) {
            course.getQuizzes().clear();
            courseRepository.save(course); // Met à jour la relation en base
        }
        courseRepository.delete(course);
    }

    public byte[] getCoursePdf(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (!"active".equals(course.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active course not found");
        }

        if (course.getPdfContent() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No PDF found for this course");
        }

        return course.getPdfContent();
    }

    public List<CourseDTO> searchActiveCourses(String keyword) {
        return courseRepository.searchActiveCourses(keyword).stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<CourseDTO> getCoursesByFormationId(Long formationId) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Formation not found"));
        return courseRepository.findByFormation(formation).stream()
                .map(this::convertToDto)
                .toList();
    }

    public Page<CourseDTO> advancedSearch(Long formationId, String status, String keyword, Pageable pageable) {
        return courseRepository.advancedSearch(formationId, status, keyword, pageable)
                .map(this::convertToDto);
    }

    // Méthodes privées utilitaires
    private void validateCourse(CourseDTO courseDTO) {
        if (courseRepository.existsByTitleAndStatus(courseDTO.getTitle(), "active")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course title already exists");
        }
    }

    private Course getExistingCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    private void setFormationIfProvided(CourseDTO dto, Course course) {
        if (dto.getFormationId() != null) {
            Formation formation = formationRepository.findById(dto.getFormationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Formation not found"));
            course.setFormation(formation);
        }
    }

    private void storePdfIfProvided(CourseDTO dto, Course course) throws IOException {
        if (dto.getPdfFile() != null && !dto.getPdfFile().isEmpty()) {
            course.setPdfContent(dto.getPdfFile().getBytes());
            course.setPdfContentType(dto.getPdfFile().getContentType());
        }
    }

    private void updatePdfIfProvided(CourseDTO dto, Course course) throws IOException {
        if (dto.getPdfFile() != null && !dto.getPdfFile().isEmpty()) {
            course.setPdfContent(dto.getPdfFile().getBytes());
            course.setPdfContentType(dto.getPdfFile().getContentType());
        }
    }

    private void processYoutubeLink(CourseDTO dto, Course course) {
        if (dto.getYoutubeLink() != null && !dto.getYoutubeLink().isEmpty()) {
            course.setYoutubeLink(extractYoutubeId(dto.getYoutubeLink()));
        }
    }

    private void updateCourseFields(Course existing, CourseDTO updated) {
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setDuration(updated.getDuration());
    }

    private CourseDTO convertToDto(Course course) {
        return enrichWithPdfUrl(courseMapper.toDto(course));
    }

    private CourseDTO enrichWithPdfUrl(CourseDTO dto) {
        // Set PDF URL if the course has a PDF file
        if (dto.getId() != null) {
            dto.setPdfUrl("/api/courses/" + dto.getId() + "/pdf");
        }
        return dto;
    }

    private String extractYoutubeId(String youtubeLink) {
        // Implémentation simplifiée - à adapter
        if (youtubeLink.contains("v=")) {
            return youtubeLink.split("v=")[1].split("&")[0];
        }
        return youtubeLink;
    }
}
