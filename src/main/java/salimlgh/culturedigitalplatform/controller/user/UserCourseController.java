package salimlgh.culturedigitalplatform.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salimlgh.culturedigitalplatform.dtos.CourseDTO;

import salimlgh.culturedigitalplatform.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Course Access", description = "APIs for accessing published courses")
public class UserCourseController {
    private final CourseService courseService;

    @Operation(summary = "Get all published courses", description = "Retrieves all courses that are marked as published")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    })
    @GetMapping("")
    public ResponseEntity<List<CourseDTO>> getAllPublishedCourses() {
        return ResponseEntity.ok(courseService.getAllActiveCourses());
    }

    @Operation(summary = "Get a published course by ID", description = "Retrieves a specific published course by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Course not available or not published"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getPublishedCourse(
            @Parameter(description = "ID of the course to retrieve", required = true)
            @PathVariable Long id) throws Exception {
        CourseDTO course = courseService.getActiveCourseById(id);
        if (!"active".equals(course.getStatus())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(course);
    }

    @Operation(summary = "Search published courses", description = "Searches for published courses matching the provided keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchPublishedCourses(
            @Parameter(description = "Keyword to search for in courses", required = true)
            @RequestParam String keyword) {
        List<CourseDTO> results = courseService.searchActiveCourses(keyword);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Get published courses by formation", description = "Retrieves all published courses associated with a specific formation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Formation not found")
    })
    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<CourseDTO>> getPublishedCoursesByFormation(
            @Parameter(description = "ID of the formation to get courses for", required = true)
            @PathVariable Long formationId) {
        List<CourseDTO> courses = courseService.getCoursesByFormationId(formationId).stream()
                .filter(CourseDTO::isActive)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Download PDF for a published course", description = "Downloads the PDF file for the specified published course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF downloaded successfully"),
            @ApiResponse(responseCode = "403", description = "Course not available or not published"),
            @ApiResponse(responseCode = "404", description = "Course or PDF not found")
    })
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @Parameter(description = "ID of the course to download PDF for", required = true)
            @PathVariable Long id) throws Exception {
        CourseDTO course = courseService.getActiveCourseById(id);
        if (!"active".equals(course.getStatus())) {
            return ResponseEntity.status(403).build();
        }

        byte[] resource = courseService.getCoursePdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + course.getTitle() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
