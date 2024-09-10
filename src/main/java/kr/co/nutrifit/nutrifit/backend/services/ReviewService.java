package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.ReviewDto;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ReviewRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Review;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Review createReview(ReviewDto reviewDto) {
        User user = userRepository.findByUsername(reviewDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Product product = productRepository.findById(reviewDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다."));

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("리뷰를 삭제할 권한이 없습니다.");
        }

        reviewRepository.delete(review);
    }

    public Page<ReviewDto> getReviewsByProduct(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdWithUsername(productId, pageable);
    }

    public Page<ReviewDto> getReviewsByUser(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdWithDto(userId, pageable);
    }
}
