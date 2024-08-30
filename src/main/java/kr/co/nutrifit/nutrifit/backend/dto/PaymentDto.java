package kr.co.nutrifit.nutrifit.backend.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.PaymentMethod;
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
public class PaymentDto {
    @NotNull
    private String orderId;

    @NotNull
    private Long total;

    @NotNull
    private Long subtotal;

    @NotNull
    private Long discount;

    @NotNull
    private Long shippingFee;

    @NotNull
    private String paymentMethod;

    @NotNull
    private List<CartItemDto> orderItems;

    @NotNull
    private OrdererDto ordererDto;

    private LocalDateTime paymentDate;

    private Long couponId;

    private int usedPoints;
}
