package kr.co.nutrifit.nutrifit.backend.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private int discountValue;

    private LocalDateTime validFrom;
    private LocalDateTime vaildUntil;

    private boolean isActive;

    private int minimumOrderAmount;
    private int maxDiscountAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;
}
