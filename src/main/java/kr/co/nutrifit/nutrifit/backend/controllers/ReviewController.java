package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.ReviewDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Review;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> createReview(
            @AuthenticationPrincipal UserAdapter userAdapter,
            @RequestBody ReviewDto reviewDto) {
        reviewService.createReview(userAdapter.getUser(), reviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰가 작성되었습니다.");
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDto>> getReviewsByProduct(@PathVariable Long productId,
                                                               Pageable pageable) {
        Page<ReviewDto> reviews = reviewService.getReviewsByProduct(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<ReviewDto>> getReviewsByUser(@AuthenticationPrincipal UserAdapter userAdapter,
                                                            Pageable pageable) {
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<ReviewDto> reviews = reviewService.getReviewsByUser(userAdapter.getUser().getId(), pageable);
        return ResponseEntity.ok(reviews);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{userId}")
    public ResponseEntity<Page<ReviewDto>> getUserReviewsByAdmin(@AuthenticationPrincipal UserAdapter userAdapter,
                                                                 @PathVariable Long userId,
                                                                 Pageable pageable) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<ReviewDto> reviews = reviewService.getReviewsByUser(userId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{reviewId}")
    public ResponseEntity<String> deleteReviewByAdmin(@AuthenticationPrincipal UserAdapter userAdapter,
                                                                 @PathVariable Long reviewId) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            reviewService.deleteReviewByAdmin(reviewId);
            return ResponseEntity.ok("리뷰가 성공적으로 삭제 되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("리뷰 삭제에 실패했습니다.");
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserAdapter userAdapter) {
        try {
            boolean deleted = reviewService.deleteReview(reviewId, userAdapter.getUser());

            if (deleted) {
                return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("리뷰 삭제 권한이 없습니다.");
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 리뷰를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 삭제 중 오류가 발생했습니다.");
        }
    }
}
