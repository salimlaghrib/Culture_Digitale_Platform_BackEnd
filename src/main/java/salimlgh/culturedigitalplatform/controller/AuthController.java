package salimlgh.culturedigitalplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import salimlgh.culturedigitalplatform.dtos.AuthRequest;
import salimlgh.culturedigitalplatform.dtos.AuthResponse;
import salimlgh.culturedigitalplatform.dtos.RegisterRequest;

import salimlgh.culturedigitalplatform.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // Restrict to your frontend origin
public class AuthController {

    private final AuthService authService;

    /**
     * Helper method to set the redirect path based on user role
     * @param response The authentication response
     */
    private void setRedirectPathBasedOnRole(AuthResponse response) {
        if (response.getRole() != null) {
            switch (response.getRole()) {
                case ADMIN:
                    response.setRedirectPath("");
                    break;
                case USER:
                    response.setRedirectPath("/sequences");
                    break;
                default:
                    response.setRedirectPath("/");
                    break;
            }
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }

        // Add redirection path based on user role
        setRedirectPathBasedOnRole(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Add redirection path based on user role
        setRedirectPathBasedOnRole(response);

        return ResponseEntity.ok(response);
    }

    // Handle validation errors
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}