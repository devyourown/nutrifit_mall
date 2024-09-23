package kr.co.nutrifit.nutrifit.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    public ReviewDto(Long id, String username, String comment, LocalDateTime createdAt, double rating, List<String> imageUrls) {
        this.id = id;
        this.username = username;
        this.comment = comment;
        this.createdAt = createdAt;
        this.rating = rating;
        this.imageUrls = imageUrls;
    }
    @NotNull
    private Long id;

    private Long productId;

    @NotNull
    private String username;

    @NotNull
    private double rating;

    private String comment;

    @NotNull
    private LocalDateTime createdAt;

    private List<String> imageUrls;
}
