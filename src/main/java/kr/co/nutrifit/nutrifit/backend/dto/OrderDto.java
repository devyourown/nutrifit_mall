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

    public OrderDto(String id, LocalDateTime orderDate, String paymentStatus, String fulfillment, Long totalAmount, String username) {
        this.id = id;
        this.orderDate = orderDate;
        this.paymentStatus = paymentStatus;
        this.fulfillment = fulfillment;
        this.totalAmount = totalAmount;
        this.username = username;
    }

    private String id;

    private Long totalAmount;

    private String paymentStatus;

    private String fulfillment;

    private String username;

    private LocalDateTime orderDate;

    private List<OrderItemDto> orderItems;

    private OrdererDto ordererDto;


}