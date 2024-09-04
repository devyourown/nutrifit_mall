package kr.co.nutrifit.nutrifit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    public OrderItemDto(Long productId, int quantity, Long totalAmount, String name) {
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.name = name;
    }

    private Long productId;
    private int quantity;
    private Long totalAmount;
    private String name;
    private String imageUrl;
}
