package salimlgh.culturedigitalplatform.mapper;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import salimlgh.culturedigitalplatform.dtos.CourseDTO;
import salimlgh.culturedigitalplatform.dtos.QuizDTO;
import salimlgh.culturedigitalplatform.entities.Course;
import salimlgh.culturedigitalplatform.entities.Quiz;

import java.util.List;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MapperCourse {

    private final MapperQuiz mapperQuiz;

    @Autowired
    public MapperCourse(MapperQuiz mapperQuiz) {
        this.mapperQuiz = mapperQuiz;
    }

    public CourseDTO toDto(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .formationId(course.getFormation() != null ? course.getFormation().getId() : null)
                .formationTitle(course.getFormation() != null ? course.getFormation().getTitle() : null)
                .duration(course.getDuration())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .pdfContentType(course.getPdfContentType())
                .youtubeLink(course.getYoutubeLink())
                .quizzes(
                course.getQuizzes() != null
                        ? course.getQuizzes().stream()
                        .map(mapperQuiz::toDto)
                        .collect(Collectors.toList())
                        : Collections.emptyList()
        )

                .build();
    }

    public Course toEntity(CourseDTO dto) {
        Course course = new Course();
        course.setId(dto.getId());
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setDuration(dto.getDuration());
        course.setStatus(dto.getStatus() != null ? dto.getStatus() : "active");
        course.setYoutubeLink(dto.getYoutubeLink());
        course.setQuizzes(
                dto.getQuizzes() != null
                        ? dto.getQuizzes().stream()
                        .map(mapperQuiz::toEntity)
                        .collect(Collectors.toList())
                        : Collections.emptyList()
        );

        return course;
    }


}