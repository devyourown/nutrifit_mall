package kr.co.nutrifit.nutrifit.backend.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {
    @NotNull
    private String code;

    private String description;

    @NotNull
    private int discountValue;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private LocalDateTime validFrom;

    @NotNull
    private LocalDateTime validUntil;

    private int minimumOrderAmount;

    private int maxDiscountAmount;

    private int remainingQuantity;

}
