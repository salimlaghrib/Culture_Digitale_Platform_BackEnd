package salimlgh.culturedigitalplatform.mapper;

import org.springframework.stereotype.Component;
import salimlgh.culturedigitalplatform.dtos.FormationDTO;
import salimlgh.culturedigitalplatform.dtos.CourseDTO;
import salimlgh.culturedigitalplatform.entities.Formation;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapperFormation {

    private final MapperCourse mapperCourse;

    public MapperFormation(MapperCourse mapperCourse) {
        this.mapperCourse = mapperCourse;
    }

    public FormationDTO toDto(Formation formation) {
        FormationDTO dto = new FormationDTO();
        dto.setId(formation.getId());
        dto.setTitle(formation.getTitle());
        dto.setSubtitle(formation.getSubtitle());
        dto.setDescription(formation.getDescription());
        dto.setImageUrl(formation.getImageUrl());
        dto.setDifficulty(formation.getDifficulty());
        dto.setCategory(formation.getCategory());
        dto.setDurationHours(formation.getDurationHours());
        dto.setCourseCount(formation.getCourses() != null ? formation.getCourses().size() : 0);
        dto.setPublished(formation.isPublished());
        dto.setTags(formation.getTags());

        if (formation.getCourses() != null) {
            List<CourseDTO> courseDTOs = formation.getCourses()
                    .stream()
                    .map(mapperCourse::toDto)
                    .collect(Collectors.toList());
            dto.setCourses(courseDTOs);
        }

        return dto;
    }

    public Formation toEntity(FormationDTO dto) {
        Formation formation = new Formation();
        formation.setId(dto.getId());
        formation.setTitle(dto.getTitle());
        formation.setSubtitle(dto.getSubtitle());
        formation.setDescription(dto.getDescription());
        formation.setImageUrl(dto.getImageUrl());
        formation.setDifficulty(dto.getDifficulty());
        formation.setCategory(dto.getCategory());
        formation.setDurationHours(dto.getDurationHours());
        formation.setPublished(dto.isPublished());
        formation.setTags(dto.getTags());
        // On ignore ici les courses pour éviter les cycles ou erreurs d'association
        return formation;
    }
}
