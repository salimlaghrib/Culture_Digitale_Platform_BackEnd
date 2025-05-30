package salimlgh.culturedigitalplatform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import salimlgh.culturedigitalplatform.entities.app_user;

@Repository
public interface UserRepository extends JpaRepository<app_user, Long> {
    Optional<app_user> findByEmail(String email);
    Boolean existsByEmail(String email);
}