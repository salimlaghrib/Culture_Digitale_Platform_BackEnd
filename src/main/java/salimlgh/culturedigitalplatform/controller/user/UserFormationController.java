package salimlgh.culturedigitalplatform.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import salimlgh.culturedigitalplatform.dtos.FormationDTO;
import salimlgh.culturedigitalplatform.entities.Formation;
import salimlgh.culturedigitalplatform.mapper.MapperFormation;
import salimlgh.culturedigitalplatform.service.FormationService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for user operations on formations.
 * Provides endpoints for viewing published formations.
 * Accessible to all users.
 */
@Slf4j
@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
@Tag(name = "Formation Access", description = "APIs for accessing published formations")
public class UserFormationController {
    private final FormationService formationService;
    private final MapperFormation mapperFormation;

    /**
     * Get all published formations.
     * 
     * @return List of published formations
     */
    @Operation(summary = "Get all published formations", description = "Retrieves all formations that are marked as published")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Formations retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<FormationDTO>> getAllPublishedFormations() {
        List<Formation> formations = formationService.getAllFormations();
        System.out.println("formations: " + formations.size() );
        return ResponseEntity.ok(
                formations.stream()
                        .map(mapperFormation::toDto)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Get a specific published formation by ID.
     * 
     * @param id The ID of the formation to retrieve
     * @return The published formation
     */
    @Operation(summary = "Get a published formation by ID", description = "Retrieves a specific published formation by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Formation retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Formation not available or not published"),
        @ApiResponse(responseCode = "404", description = "Formation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FormationDTO> getPublishedFormation(
            @Parameter(description = "ID of the formation to retrieve", required = true)
            @PathVariable Long id) {
        try {
            Formation formation = formationService.getPublishedFormationById(id);
            return ResponseEntity.ok(mapperFormation.toDto(formation));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search for published formations.
     * 
     * @param keyword The search term
     * @return List of published formations matching the search criteria
     */
    @Operation(summary = "Search published formations", description = "Searches for published formations matching the provided keyword")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<List<FormationDTO>> searchPublishedFormations(
            @Parameter(description = "Keyword to search for in formations", required = true)
            @RequestParam String keyword) {
        List<Formation> formations = formationService.searchPublishedFormations(keyword);
        return ResponseEntity.ok(
                formations.stream()
                        .map(mapperFormation::toDto)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Get published formations by difficulty level.
     * 
     * @param difficulty The difficulty level
     * @return List of published formations with the specified difficulty level
     */
    @Operation(summary = "Get published formations by difficulty", description = "Retrieves all published formations with the specified difficulty level")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Formations retrieved successfully")
    })
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<FormationDTO>> getPublishedFormationsByDifficulty(
            @Parameter(description = "Difficulty level to filter by", required = true)
            @PathVariable String difficulty) {
        List<Formation> formations = formationService.getPublishedFormationsByDifficulty(difficulty);
        return ResponseEntity.ok(
                formations.stream()
                        .map(mapperFormation::toDto)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Get published formations by category.
     * 
     * @param category The category
     * @return List of published formations in the specified category
     */
    @Operation(summary = "Get published formations by category", description = "Retrieves all published formations in the specified category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Formations retrieved successfully")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<FormationDTO>> getPublishedFormationsByCategory(
            @Parameter(description = "Category to filter by", required = true)
            @PathVariable String category) {
        List<Formation> formations = formationService.getPublishedFormationsByCategory(category);
        return ResponseEntity.ok(
                formations.stream()
                        .map(mapperFormation::toDto)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Get latest published formations.
     * 
     * @return List of published formations ordered by creation date (newest first)
     */
    @Operation(summary = "Get latest published formations", description = "Retrieves all published formations ordered by creation date (newest first)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Formations retrieved successfully")
    })
    @GetMapping("/latest")
    public ResponseEntity<List<FormationDTO>> getLatestPublishedFormations() {
        List<Formation> formations = formationService.getPublishedFormationsOrderByCreatedAtDesc();
        return ResponseEntity.ok(
                formations.stream()
                        .map(mapperFormation::toDto)
                        .collect(Collectors.toList())
        );
    }
}