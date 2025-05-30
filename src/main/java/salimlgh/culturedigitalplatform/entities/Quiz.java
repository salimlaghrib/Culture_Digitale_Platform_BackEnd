package salimlgh.culturedigitalplatform.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter @Setter
@NoArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id") // Pas de unique=true
    @JsonIgnore
    private Course course;

    @ElementCollection(fetch = FetchType.EAGER) // Force le chargement immédiat
    @CollectionTable(name = "quiz_answers", joinColumns = @JoinColumn(name = "quiz_id"))
    @Column(name = "answer")
    private List<String> answers = new ArrayList<>();

    private Integer correctAnswerIndex;
}