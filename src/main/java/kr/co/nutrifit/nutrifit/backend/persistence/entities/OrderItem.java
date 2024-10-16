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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_item", indexes = {
        @Index(name = "idx_order_payment_id", columnList = "orderPaymentId"),
        @Index(name = "idx_order_id_product_name", columnList = "orderPaymentId, productName"),
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_order_date", columnList = "orderDate"),
        @Index(name = "idx_current_status", columnList = "currentStatus"),
        @Index(name = "idx_tracking_number", columnList = "trackingNumber")
})
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String orderPaymentId;

    @Column(nullable = true)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Long totalAmount;

    @Column
    private String trackingNumber;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private LocalDateTime currentStatusTime;

    @Column(nullable = false)
    private String currentStatus;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientPhone;

    @Column(nullable = false)
    private String ordererName;

    @Column(nullable = false)
    private String ordererPhone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String addressDetail;

    @Column
    private String cautions;
}
