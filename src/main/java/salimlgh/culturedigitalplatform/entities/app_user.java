package salimlgh.culturedigitalplatform.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class app_user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date dateOfBirth;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public String getUsername() {
        return email;
    }

    public enum UserRole {
        USER,    // Pour les utilisateurs normaux (redirection vers /cours)
        ADMIN    // Pour les administrateurs (redirection vers /administration)
    }

    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Ajoutez un constructeur ou une méthode d'initialisation pour définir createdAt
    @jakarta.persistence.PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

}