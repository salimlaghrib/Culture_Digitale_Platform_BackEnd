package salimlgh.culturedigitalplatform.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import salimlgh.culturedigitalplatform.entities.app_user;
import salimlgh.culturedigitalplatform.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<app_user> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<app_user> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public app_user createUser(app_user user) {
        // Hash le mot de passe si présent
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public Optional<app_user> updateUser(Long id, app_user user) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(user.getFirstName());
                    existing.setEmail(user.getEmail());
                    existing.setRole(user.getRole());
                    // Ajoute d'autres champs à mettre à jour si besoin
                    return userRepository.save(existing);
                });
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public Optional<app_user> activateUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setActive(true);
                    return userRepository.save(user);
                });
    }

    public Optional<app_user> deactivateUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setActive(false);
                    return userRepository.save(user);
                });
    }
}
