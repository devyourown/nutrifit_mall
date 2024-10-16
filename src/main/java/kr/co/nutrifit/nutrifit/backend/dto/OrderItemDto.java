package kr.co.nutrifit.nutrifit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
public class OrderItemDto {
    public OrderItemDto(Long productId, String name, String imageUrl, Long price, int quantity, Long totalAmount) {
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public OrderItemDto(Long productId, String name, String imageUrl, Long price, int quantity, Long totalAmount, LocalDateTime orderDate) {
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.orderDate = orderDate;
    }

    private Long productId;
    private String name;
    private String imageUrl;
    private Long price;
    private int quantity;
    private Long totalAmount;
    private LocalDateTime orderDate;
}
