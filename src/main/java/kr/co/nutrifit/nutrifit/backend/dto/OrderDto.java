package kr.co.nutrifit.nutrifit.backend.dto;

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
public class OrderDto {

    public OrderDto(String id, LocalDateTime orderDate, String fulfillment, String username, String trackingNumber, String productName) {
        this.id = id;
        this.orderDate = orderDate;
        this.fulfillment = fulfillment;
        this.username = username;
        this.trackingNumber = trackingNumber;
        this.productName = productName;
    }

    public OrderDto(String id, LocalDateTime orderDate, String fulfillment, String trackingNumber, Long totalAmount, String productName) {
        this.id = id;
        this.orderDate = orderDate;
        this.fulfillment = fulfillment;
        this.trackingNumber = trackingNumber;
        this.totalAmount = totalAmount;
        this.productName = productName;
    }

    private String id;

    private Long totalAmount;

    private String fulfillment;

    private String username;

    private LocalDateTime orderDate;

    private List<OrderItemDto> orderItems;

    private String trackingNumber;

    private String productName;


}