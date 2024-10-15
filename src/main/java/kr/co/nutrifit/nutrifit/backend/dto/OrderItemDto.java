package kr.co.nutrifit.nutrifit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
public class OrderItemDto {
    public OrderItemDto(Long productId, String name, String imageUrl, int price, int quantity, Long totalAmount) {
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    private Long productId;
    private String name;
    private String imageUrl;
    private int price;
    private int quantity;
    private Long totalAmount;
}
