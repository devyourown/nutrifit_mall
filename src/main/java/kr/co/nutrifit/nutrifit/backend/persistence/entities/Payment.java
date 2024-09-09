package kr.co.nutrifit.nutrifit.backend.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderPaymentId;

    @Column(nullable = false)
    private Long total;

    @Column(nullable = false)
    private Long subtotal;

    @Column(nullable = false)
    private Long discount;

    @Column(nullable = false)
    private Long shippingFee;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private String paymentStatus;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
    private Order order;

    private Long couponId;

    private int usedPoints;
}
