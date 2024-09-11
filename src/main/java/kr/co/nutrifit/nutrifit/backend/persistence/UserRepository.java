package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {})
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @EntityGraph(attributePaths = {})
    Optional<User> findByEmail(String email);
}
