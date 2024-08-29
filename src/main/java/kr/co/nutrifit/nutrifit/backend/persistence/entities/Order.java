package kr.co.nutrifit.nutrifit.backend.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_id")
    private Shipping shipping;

    @PrePersist
    public void onOrder() {
        this.orderDate = LocalDateTime.now();
    }

    public void addOrderItem(OrderItem orderItem) {
        if (orderItems == null)
            orderItems = new ArrayList<>();
        orderItem.setOrder(this);
        orderItems.add(orderItem);
    }

    public void removeOrderItem(OrderItem orderItem) {
        if (orderItems != null) {
            orderItems.remove(orderItem);
            orderItem.setOrder(null);
        }
    }
}
