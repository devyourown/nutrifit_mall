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
    public PaymentDto(String orderId, Long total, Long subtotal, Long discount, Long shippingFee,
                      String paymentMethod, String recipientName, String recipientPhone,
                      String ordererName, String ordererPhone, String address, String addressDetail,
                      String cautions, LocalDateTime paymentDate, String couponCode,
                      int usedPoints, long earnPoints, String phoneNumber) {
        this.orderId = orderId;
        this.total = total;
        this.subtotal = subtotal;
        this.discount = discount;
        this.shippingFee = shippingFee;
        this.paymentMethod = paymentMethod;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.ordererName = ordererName;
        this.ordererPhone = ordererPhone;
        this.address = address;
        this.addressDetail = addressDetail;
        this.cautions = cautions;
        this.paymentDate = paymentDate;
        this.couponCode = couponCode;
        this.usedPoints = usedPoints;
        this.earnPoints = earnPoints;
        this.phoneNumber = phoneNumber;
    }

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
    private List<OrderItemDto> orderItems;

    @NotNull
    private OrdererDto ordererDto;

    private LocalDateTime paymentDate;

    private String couponCode;

    private int usedPoints;

    private long earnPoints;

    private String phoneNumber;

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
