package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Cart;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.CartItem;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.CartItemDto(" +
            "ci.id, p.name, p.description, p.originalPrice, ci.imageUrl, ci.quantity) " +
            "FROM CartItem ci " +
            "JOIN ci.product p " +
            "WHERE ci.cart = :cart")
    List<CartItemDto> findByCart(@Param("cart") Cart cart);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
