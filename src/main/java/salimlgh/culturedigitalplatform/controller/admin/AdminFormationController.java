package salimlgh.culturedigitalplatform.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import salimlgh.culturedigitalplatform.dtos.FormationDTO;
import salimlgh.culturedigitalplatform.entities.Formation;
import salimlgh.culturedigitalplatform.mapper.MapperFormation;
import salimlgh.culturedigitalplatform.service.FormationService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for admin operations on formations.
 * Provides endpoints for managing formations (CRUD operations).
 * Accessible only to users with ADMIN role.
 */
@RestController
@RequestMapping("/api/admin/formations")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Formation Management", description = "APIs for managing formations (admin only)")
public class AdminFormationController {
    private final FormationService formationService;
    private final MapperFormation mapperFormation;

    /**
     * Get all formations.
     * 
     * @return List of all formations
     */
    @Operation(summary = "Get all formations", description = "Retrieves all formations regardless of publication status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Formations retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<FormationDTO>> getAllFormations() {
        List<Formation> formations = formationService.findAll();
        return ResponseEntity.ok(
                formations.stream()
                        .map(mapperFormation::toDto)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Get a specific formation by ID.
     * 
     * @param id The ID of the formation to retrieve
     * @return The formation
     */
    @Operation(summary = "Get a formation by ID", description = "Retrieves a specific formation by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Formation retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Formation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FormationDTO> getFormationById(
            @Parameter(description = "ID of the formation to retrieve", required = true)
            @PathVariable Long id) {
        try {
            Formation formation = formationService.getFormationById(id);
            return ResponseEntity.ok(mapperFormation.toDto(formation));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new formation.
     * 
     * @param dto The formation data
     * @return The created formation
     */
    @Operation(summary = "Create a new formation", description = "Creates a new formation with the provided data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Formation created successfully")
    })
    @PostMapping
    public ResponseEntity<FormationDTO> createFormation(
            @Parameter(description = "Formation data", required = true)
            @Valid @RequestBody FormationDTO dto) {
        Formation formation = mapperFormation.toEntity(dto);
        Formation savedFormation = formationService.createFormation(formation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapperFormation.toDto(savedFormation));
    }

    /**
     * Update an existing formation.
     * 
     * @param id The ID of the formation to update
     * @param dto The updated formation data
     * @return The updated formation
     */
    @Operation(summary = "Update a formation", description = "Updates an existing formation with the provided data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Formation updated successfully"),
        @ApiResponse(responseCode = "404", description = "Formation not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FormationDTO> updateFormation(
            @Parameter(description = "ID of the formation to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated formation data", required = true)
            @Valid @RequestBody FormationDTO dto) {
        try {
            Formation formation = mapperFormation.toEntity(dto);
            Formation updatedFormation = formationService.updateFormation(id, formation);
            return ResponseEntity.ok(mapperFormation.toDto(updatedFormation));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a formation.
     * 
     * @param id The ID of the formation to delete
     * @return No content
     */
    @Operation(summary = "Delete a formation", description = "Deletes a formation with the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Formation deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Formation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormation(
            @Parameter(description = "ID of the formation to delete", required = true)
            @PathVariable Long id) {
        try {
            formationService.deleteFormation(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Publish or unpublish a formation.
     * 
     * @param id The ID of the formation to publish/unpublish
     * @param published Whether to publish (true) or unpublish (false) the formation
     * @return The updated formation
     */
    @Operation(summary = "Publish or unpublish a formation", description = "Changes the publication status of a formation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Formation publication status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Formation not found")
    })
    @PutMapping("/{id}/publish")
    public ResponseEntity<FormationDTO> publishFormation(
            @Parameter(description = "ID of the formation to publish/unpublish", required = true)
            @PathVariable Long id,
            @Parameter(description = "Publication status (true to publish, false to unpublish)", required = true)
            @RequestParam boolean published) {
        try {
            Formation formation = formationService.publishFormation(id, published);
            return ResponseEntity.ok(mapperFormation.toDto(formation));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}