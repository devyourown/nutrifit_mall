package kr.co.nutrifit.nutrifit.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    @NotNull
    private Long id;

    private Long productId;

    @NotNull
    private String username;

    @NotNull
    private int rating;

    private String comment;

    @NotNull
    private LocalDateTime createdAt;
}
