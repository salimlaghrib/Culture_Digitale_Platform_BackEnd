package salimlgh.culturedigitalplatform.dtos;


import lombok.Data;
import salimlgh.culturedigitalplatform.entities.DifficultyLevel;
import java.util.List;
@Data
public class FormationDTO {
        private Long id;
        private String title;
        private String subtitle;
        private String description;
        private String imageUrl;
        private DifficultyLevel difficulty;
        private String category;
        private Integer durationHours;
        private Integer courseCount; // Calculé dynamiquement
        private boolean published;
        private List<String> tags;
        private List<CourseDTO> courses;

        // Getters, Setters
    }
