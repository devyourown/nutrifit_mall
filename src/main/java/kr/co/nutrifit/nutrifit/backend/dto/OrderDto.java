package kr.co.nutrifit.nutrifit.backend.dto;

import jakarta.persistence.*;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.OrderItem;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Payment;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Shipping;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
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
    private String id;

    private Long totalAmount;

    private LocalDateTime orderDate;

    private List<OrderItemDto> orderItems;
}