package salimlgh.culturedigitalplatform.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import salimlgh.culturedigitalplatform.entities.app_user;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private Long id;
    private String email;
    private app_user.UserRole role;  // Utilise l'enum UserRole de app_user
    private String message;
    private boolean success;
    private String redirectPath;     // Pour la redirection après connexion
}