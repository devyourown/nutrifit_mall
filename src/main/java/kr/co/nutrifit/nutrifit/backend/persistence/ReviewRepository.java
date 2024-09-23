package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.ReviewDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.ReviewDto(r.id, u.username, r.comment, r.createdAt, r.rating, r.imageUrls) " +
            "FROM Review r " +
            "JOIN r.user u " +
            "WHERE r.product.id = :productId")
    Page<ReviewDto> findByProductIdWithUsername(@Param("productId") Long productId, Pageable pageable);
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.ReviewDto(r.id, u.username, r.comment, r.createdAt, r.rating, r.imageUrls) " +
            "FROM Review r " +
            "JOIN r.user u " +
            "WHERE u.id = :userId")
    Page<ReviewDto> findByUserIdWithDto(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.id = :reviewId")
    Optional<Review> findByIdWithUser(@Param("reviewId") Long reviewId);

}
