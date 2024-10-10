package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.CartItemDto(" +
            "ci.id, p.name, p.description, p.originalPrice, ci.imageUrl, ci.quantity, p.id) " +
            "FROM CartItem ci " +
            "JOIN ci.product p " +
            "WHERE ci.cart.user.id = :userId")
    Page<CartItemDto> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
