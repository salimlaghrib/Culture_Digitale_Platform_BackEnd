package salimlgh.culturedigitalplatform.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    @JsonIgnoreProperties("courses")
    private Formation formation;

    private Integer duration; // en minutes

    @Builder.Default
    @Column(nullable = false)
    private String status = "active"; // 'active', 'indicative'

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "pdf_content", columnDefinition = "LONGBLOB")
    private byte[] pdfContent;

    @Column(name = "pdf_content_type")
    private String pdfContentType;

    @Column(name = "youtube_link")
    private String youtubeLink;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Évite les boucles infinies en JSON
    private List<Quiz> quizzes = new ArrayList<>();

    // Méthodes utilitaires
    public boolean hasPdf() {
        return pdfContent != null && pdfContent.length > 0;
    }

    public boolean hasVideo() {
        return youtubeLink != null && !youtubeLink.isEmpty();
    }

    // Méthode helper pour synchroniser les deux côtés de la relation
    public void setQuiz(Quiz quiz) {
        if (quiz == null) {
            if (this.quizzes != null) {
                this.quizzes.get(0).setCourse(null);
            }
        } else {
            quiz.setCourse(this);
        }
        this.quizzes = Collections.singletonList(quiz);
    }
}