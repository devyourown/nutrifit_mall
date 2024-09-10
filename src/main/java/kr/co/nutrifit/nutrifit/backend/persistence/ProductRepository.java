package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.ProductDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.ProductDto(p.id, p.name, p.description, p.category, p.stockQuantity, p.lowStockThreshold, p.imageUrls, p.badgeTexts, p.originalPrice, p.discountedPrice, p.reviewRating, p.reviewCount) " +
            "FROM Product p")
    Page<ProductDto> findAllToDto(Pageable pageable);
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.options o " +
            "LEFT JOIN FETCH p.productDetail pd " +
            "WHERE p.id = :id")
    Optional<Product> findByIdWithOptionsAndDetail(@Param("id") Long id);
    List<Product> findByCategory(String category);
    List<Product> findByIdIn(List<Long> ids);
}
