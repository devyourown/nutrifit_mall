package kr.co.nutrifit.nutrifit.backend.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true,  nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private String imageUrl;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Point point;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PointTransaction> transactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCoupon> coupons; // 사용자가 소유한 쿠폰 목록

    @Column
    private String address;
    @Column
    private String addressDetails;
    @Column
    private String shippingDetails;

    public void addOrder(Order order) {
        if (orders == null)
            orders = new ArrayList<>();
        orders.add(order);
        order.setUser(this);
    }

    public void removeOrder(Order order) {
        if (orders != null) {
            orders.remove(order);
            order.setUser(null);
        }
    }

    public void addPointTransaction(PointTransaction pointTransaction) {
        if (transactions == null)
            transactions = new ArrayList<>();
        transactions.add(pointTransaction);
        pointTransaction.setUser(this);
    }

    public void removePointTransaction(PointTransaction pointTransaction) {
        if (transactions != null) {
            transactions.remove(pointTransaction);
            pointTransaction.setUser(null);
        }
    }

    public void addUserCoupon(UserCoupon coupon) {
        if (coupons == null)
            coupons = new ArrayList<>();
        coupons.add(coupon);
        coupon.setUser(this);
    }

    public void removeCoupon(UserCoupon coupon) {
        if (coupons != null) {
            coupons.remove(coupon);
            coupon.setUser(null);
        }
    }
}
