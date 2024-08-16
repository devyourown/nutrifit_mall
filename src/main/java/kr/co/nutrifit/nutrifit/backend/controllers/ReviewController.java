package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.persistence.entities.Review;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @AuthenticationPrincipal UserAdapter userAdapter,
            @RequestParam Long productId,
            @RequestParam int rating,
            @RequestParam(required = false) String comment) {
        Review review = reviewService.createReview(userAdapter.getUser().getId(), productId, rating, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Review>> getReviewsByUser(@AuthenticationPrincipal UserAdapter userAdapter) {
        List<Review> reviews = reviewService.getReviewsByUser(userAdapter.getUser().getId());
        return ResponseEntity.ok(reviews);
    }
}
