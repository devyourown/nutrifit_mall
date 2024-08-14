package kr.co.nutrifit.nutrifit.backend.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingStatus shippingStatus;

    @Column
    private LocalDateTime orderDate;

    @Column
    private LocalDateTime pendingDate;

    @Column
    private LocalDateTime shippedDate;

    @Column
    private LocalDateTime deliveredDate;

    @Column
    private LocalDateTime cancelledDate;

    @Column
    private LocalDateTime refundDate;

}
