package salimlgh.culturedigitalplatform.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;
import salimlgh.culturedigitalplatform.entities.Quiz;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
    private String description;

    private Long formationId;
    private String formationTitle; // Pour l'affichage

    @Min(value = 1, message = "La durée doit être d'au moins 1 minute")
    private Integer duration;

    @Pattern(regexp = "active|inactive", message = "Le statut doit être 'active' ou 'inactive'")
    private String status;

    @NotNull(message = "The createdAt field is required.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;


    // Pour l'upload
    private MultipartFile pdfFile;

    // Pour la réponse
    private String pdfContentType;
    private String pdfUrl;

    @URL(message = "Le lien YouTube doit être une URL valide")
    private String youtubeLink;

    private List<QuizDTO> quizzes;

    // Méthodes utilitaires
    public boolean isActive() {
        return "active".equals(this.status);
    }

    public boolean hasPdf() {
        return this.pdfUrl != null || this.pdfFile != null;
    }
}