package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.OrdererDto;
import kr.co.nutrifit.nutrifit.backend.dto.UserDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrdererDto( " +
            "u.orderer.recipientName, " +
            "u.orderer.recipientPhone, " +
            "u.orderer.ordererName, " +
            "u.orderer.ordererPhone, " +
            "u.orderer.address, " +
            "u.orderer.addressDetail, " +
            "u.orderer.cautions) " +
            "FROM User u WHERE u = :user")
    Optional<OrdererDto> findOrdererDtoByUser(@Param("user") User user);
}
