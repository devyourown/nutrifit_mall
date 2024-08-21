package kr.co.nutrifit.nutrifit.review;

import kr.co.nutrifit.nutrifit.backend.dto.ReviewDto;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ReviewRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Review;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Product product;
    private Review review;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("testuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        product = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Description")
                .stockQuantity(10)
                .build();

        review = Review.builder()
                .id(1L)
                .user(user)
                .product(product)
                .rating(5)
                .comment("Great product!")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createReview_ShouldSaveReview() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ReviewDto reviewDto = ReviewDto.builder()
                .username(user.getUsername())
                .productId(product.getId())
                .rating(5)
                .comment("Great product!")
                .build();

        reviewService.createReview(reviewDto);

    }

    @Test
    void deleteReview_ShouldRemoveReview() {
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        reviewService.deleteReview(review.getId(), user.getId());

        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void getReviewsByProduct_ShouldReturnReviews() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(reviewRepository.findByProduct(product)).thenReturn(List.of(review));

        List<ReviewDto> reviews = reviewService.getReviewsByProduct(product.getId());

        assertEquals(1, reviews.size());
        verify(reviewRepository, times(1)).findByProduct(product);
    }

    @Test
    void getReviewsByUser_ShouldReturnReviews() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(reviewRepository.findByUser(user)).thenReturn(List.of(review));

        List<ReviewDto> reviews = reviewService.getReviewsByUser(user.getId());

        assertEquals(1, reviews.size());
        verify(reviewRepository, times(1)).findByUser(user);
    }
}
