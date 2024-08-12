package kr.co.nutrifit.nutrifit.backend.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long productId;
    private int quantity;
}
