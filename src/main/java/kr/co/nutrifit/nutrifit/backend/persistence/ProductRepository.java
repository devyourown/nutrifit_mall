package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.OptionDto;
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
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.ProductDto(p.id, p.name, p.description, p.category, p.stockQuantity, p.lowStockThreshold, p.imageUrls, p.badgeTexts, p.originalPrice, p.discountedPrice, p.reviewRating, p.reviewCount, p.isReleased) " +
            "FROM Product p")
    Page<ProductDto> findAllToDto(Pageable pageable);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.ProductDto(p.id, p.name, p.description, p.category, p.stockQuantity, p.lowStockThreshold, p.imageUrls, p.badgeTexts, p.originalPrice, p.discountedPrice, p.reviewRating, p.reviewCount) " +
            "FROM Product p WHERE p.isReleased = true")
    Page<ProductDto> findReleasedProducts(Pageable pageable);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.ProductDto(p.id, p.name, p.description, p.category, p.stockQuantity, p.lowStockThreshold, p.imageUrls, p.badgeTexts, p.originalPrice, p.discountedPrice, p.reviewRating, p.reviewCount, p.isReleased) " +
            "FROM Product p WHERE p.id = :id and p.isReleased = true")
    Optional<ProductDto> findProductDtoById(@Param("id") Long id);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OptionDto(o.id, o.quantity, o.price, o.description) " +
            "FROM Options o WHERE o.product.id = :productId")
    List<OptionDto> findOptionsByProductId(@Param("productId") Long productId);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.ProductDto(p.id, p.name, p.description, p.category, p.stockQuantity, p.lowStockThreshold, p.imageUrls, p.badgeTexts, p.originalPrice, p.discountedPrice, p.reviewRating, p.reviewCount, p.isReleased) " +
            "FROM Product p WHERE p.isReleased = true and p.category = :category")
    Page<ProductDto> findProductsByCategory(Pageable pageable, @Param("category") String category);


    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    List<Product> findAllById(@Param("ids") List<Long> ids);
}
