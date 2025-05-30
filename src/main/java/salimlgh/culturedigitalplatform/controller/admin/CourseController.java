package salimlgh.culturedigitalplatform.controller.admin;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import salimlgh.culturedigitalplatform.dtos.CourseDTO;
import salimlgh.culturedigitalplatform.service.CourseService;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCourse(
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart(value = "pdfFile", required = false) MultipartFile pdfFile,
            @RequestPart("duration") String durationStr,
            @RequestPart("status") String status,
            @RequestPart("createdAt") String createdAtStr,
            @RequestPart(value = "formationId", required = false ) String formationIdStr,
            @RequestPart(value = "youtubeLink", required = false) String youtubeLink) throws Exception {

        // Conversion des types
        int duration = Integer.parseInt(durationStr);
        Long formationId = formationIdStr != null && !formationIdStr.isEmpty() ? Long.parseLong(formationIdStr) : null;


        // Construction du DTO
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setTitle(title);
        courseDTO.setDescription(description);
        courseDTO.setDuration(duration);
        courseDTO.setStatus(status);
        courseDTO.setCreatedAt(LocalDateTime.now());
        courseDTO.setFormationId(formationId);
        courseDTO.setYoutubeLink(youtubeLink);
        courseDTO.setPdfFile(pdfFile);  // Set the PDF file in the DTO

        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(
            @PathVariable Long id) {
        CourseDTO course = courseService.getActiveCourseById(id);
        return ResponseEntity.ok(course);
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllActiveCourses() {
        List<CourseDTO> courses = courseService.getAllActiveCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<CourseDTO>> getAllActiveCoursesPaginated(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<CourseDTO> courses = courseService.getAllActiveCourses(pageable);
        return ResponseEntity.ok(courses);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile,
            @RequestParam("createdAt") String createdAt,
            @RequestParam("duration") Integer duration,
            @RequestParam("status") String status,
            @RequestParam("formationId") Long formationId,
            @RequestParam(value = "youtubeLink", required = false) String youtubeLink
    ) throws IOException {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setTitle(title);
        courseDTO.setDescription(description);
        courseDTO.setCreatedAt(LocalDateTime.now());
        courseDTO.setDuration(duration);
        courseDTO.setStatus(status);
        courseDTO.setFormationId(formationId);
        courseDTO.setYoutubeLink(youtubeLink);

        courseDTO.setPdfFile(pdfFile);

        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(updatedCourse);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateCourseStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        if ("active".equalsIgnoreCase(status)) {
            courseService.activateCourse(id);
        } else if ("inactive".equalsIgnoreCase(status)) {
            courseService.deactivateCourse(id);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pdf")
    public ResponseEntity<CourseDTO> uploadPdf(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setPdfFile(file);
        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(updatedCourse);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getCoursePdf(
            @PathVariable Long id) {
        byte[] pdfContent = courseService.getCoursePdf(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .body(pdfContent);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchCourses(
            @RequestParam String keyword) {
        List<CourseDTO> results = courseService.searchActiveCourses(keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<Page<CourseDTO>> advancedSearch(
            @RequestParam(required = false) Long formationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<CourseDTO> results = courseService.advancedSearch(
                formationId, status, keyword, pageable);
        return ResponseEntity.ok(results);
    }
}
