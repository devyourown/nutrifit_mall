package kr.co.nutrifit.nutrifit.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.nutrifit.nutrifit.backend.controllers.ReviewController;
import kr.co.nutrifit.nutrifit.backend.dto.ReviewDto;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ReviewRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Review;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewController reviewController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Product product;
    private ReviewDto reviewDto;
    private Review review;
    private UserAdapter userAdapter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        user = userRepository.save(user);

        product = Product.builder()
                .name("Product 1")
                .description("Description")
                .price(1000L)
                .stockQuantity(10)
                .imageUrl("imageUrl")
                .build();

        review = Review.builder()
                .user(user)
                .rating(5)
                .comment("Great product!")
                .createdAt(LocalDateTime.now())
                .build();

        product = productRepository.save(product);
        review.setProduct(product);
        review = reviewRepository.save(review);


        reviewDto = ReviewDto.builder()
                .username(user.getUsername())
                .productId(product.getId())
                .rating(5)
                .comment("Great product!")
                .createdAt(LocalDateTime.now())
                .build();

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        userAdapter = new UserAdapter(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userAdapter, null, userAdapter.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void createReview_ShouldReturn201Status() throws Exception {
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto))
                        .with(SecurityMockMvcRequestPostProcessors.user(new UserAdapter(user))))
                .andExpect(status().isCreated())
                .andExpect(content().string("리뷰가 작성되었습니다."));
    }

    @Test
    void getReviewsByProduct_ShouldReturnReviews() throws Exception {
        mockMvc.perform(get("/api/reviews/product/{productId}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Great product!"));
    }

    @Test
    void getReviewsByUser_ShouldReturnReviews() throws Exception {
        mockMvc.perform(get("/api/reviews/user")
                        .with(SecurityMockMvcRequestPostProcessors.user(new UserAdapter(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Great product!"));
    }

    @Test
    void deleteReview_ShouldReturn204Status() throws Exception {
        mockMvc.perform(delete("/api/reviews/{reviewId}", review.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(new UserAdapter(user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
