package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ReviewRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Review;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Review createReview(Long userId, Long productId, int rating, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다."));

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다."));
        return reviewRepository.findByProduct(product);
    }

    public List<Review> getReviewsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        return reviewRepository.findByUser(user);
    }
}
