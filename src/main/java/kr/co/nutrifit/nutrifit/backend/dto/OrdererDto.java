package kr.co.nutrifit.nutrifit.backend.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ShippingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdererDto {
    @NotNull
    private String recipientName;

    @NotNull
    private String recipientPhone;

    @NotNull
    private String ordererName;

    @NotNull
    private String ordererPhone;

    @NotNull
    private String address;

    @NotNull
    private String addressDetail;

    private String cautions;
}
