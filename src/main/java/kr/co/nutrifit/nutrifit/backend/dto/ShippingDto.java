package kr.co.nutrifit.nutrifit.backend.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ShippingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDto {
    @NotNull
    private Long id;

    @NotNull
    private Long orderId;

    @NotNull
    private String recipientName;

    @NotNull
    private String address;

    @NotNull
    private String phoneNumber;

    private ShippingStatus currentStatus;
}
