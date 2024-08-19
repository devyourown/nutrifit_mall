package kr.co.nutrifit.nutrifit.backend.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    @NotNull
    private Long orderId;

    @NotNull
    private Long amount;

    @NotNull
    private String paymentMethod;

    @NotNull
    private String impUid;

    @NotNull
    private List<OrderItemDto> orderItems;

    @NotNull
    private ShippingDto shippingDto;

    private Long couponId;

    private int usedPoints;
}
