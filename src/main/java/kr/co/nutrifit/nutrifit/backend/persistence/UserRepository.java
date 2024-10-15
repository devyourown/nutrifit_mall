package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.OrdererDto;
import kr.co.nutrifit.nutrifit.backend.dto.UserDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrdererDto( " +
            "u.recipientName, " +
            "u.recipientPhone, " +
            "u.ordererName, " +
            "u.ordererPhone, " +
            "u.address, " +
            "u.addressDetail, " +
            "u.cautions) " +
            "FROM User u WHERE u = :user")
    Optional<OrdererDto> findOrdererDtoByUser(@Param("user") User user);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.UserDto(" +
            "u.id, u.username, u.email, u.profileImage, u.createdAt) " +
            "FROM User u ORDER BY u.createdAt ASC")
    Page<UserDto> findAllUsersByCreatedAt(Pageable pageable);
}
