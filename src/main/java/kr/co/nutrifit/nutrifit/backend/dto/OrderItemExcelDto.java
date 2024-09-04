package kr.co.nutrifit.nutrifit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemExcelDto {
    public OrderItemExcelDto(
            String orderId,
            String ordererName,
            String ordererPhone,
            String recipientName,
            String recipientPhone,
            String address,
            String addressDetail,
            String cautions,
            String productName,
            int quantity,
            String trackingNumber) {
        this.orderId = orderId;
        this.ordererName = ordererName;
        this.ordererPhone = ordererPhone;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.address = address + " " + addressDetail;
        this.cautions = cautions;
        this.productName = productName;
        this.quantity = quantity;
        this.trackingNumber = trackingNumber;
    }
    private String orderId;
    private String productName;
    private int quantity;
    private String ordererName;
    private String ordererPhone;
    private String recipientName;
    private String recipientPhone;
    private String address;
    private String cautions;
    private String trackingNumber;
}
