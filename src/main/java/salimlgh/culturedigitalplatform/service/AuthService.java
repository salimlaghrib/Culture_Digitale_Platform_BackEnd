package salimlgh.culturedigitalplatform.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import salimlgh.culturedigitalplatform.dtos.AuthRequest;
import salimlgh.culturedigitalplatform.dtos.AuthResponse;
import salimlgh.culturedigitalplatform.dtos.RegisterRequest;
import salimlgh.culturedigitalplatform.entities.app_user;
import salimlgh.culturedigitalplatform.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Vérification des doublons
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Email déjà utilisé")
                    .build();
        }

        // Création de l'utilisateur
        app_user user = app_user.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getBirthDate())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        app_user savedUser = userRepository.save(user);

        return AuthResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .success(true)
                .message("Inscription réussie")
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            app_user user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            return AuthResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .success(true)
                    .message("Connexion réussie")
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Échec de connexion: " + e.getMessage())
                    .build();
        }
    }

    // Méthodes admin
    public List<app_user> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<app_user> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public app_user updateUserRole(Long id, String role) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setRole(app_user.UserRole.valueOf(role));
                    return userRepository.save(user);
                })
                .orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public long getUserCount() {
        return userRepository.count();
    }
}
