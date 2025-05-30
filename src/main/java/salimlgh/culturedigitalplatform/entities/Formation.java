package salimlgh.culturedigitalplatform.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Formation.java
@Entity
@Getter @Setter
@NoArgsConstructor
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "subtitle") // "soustitle" en français
    private String subtitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficulty; // Enum pour les difficultés

    private String category;

    @Column(name = "duration_hours")
    private Integer durationHours; // Durée en heures

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "published")
    private boolean published = false;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Course> courses = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "formation_tags", joinColumns = @JoinColumn(name = "formation_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
}
