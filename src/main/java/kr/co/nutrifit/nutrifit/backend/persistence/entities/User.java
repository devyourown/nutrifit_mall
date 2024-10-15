package kr.co.nutrifit.nutrifit.backend.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @Column(nullable = true)
    private String password;

    @Column(nullable = true)
    private Boolean oAuth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column
    private String profileImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CartItem> items;

    private String recipientName;

    private String recipientPhone;

    private String ordererName;

    private String ordererPhone;

    private String address;

    private String addressDetail;

    private String cautions;

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
}
