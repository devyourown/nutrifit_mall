package kr.co.nutrifit.nutrifit.backend.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    public void addCartItem(CartItem item) {
        if (cartItems == null)
            cartItems = new ArrayList<>();
        cartItems.add(item);
        item.setCart(this);
    }

    public void removeCartItem(CartItem item) {
        if (cartItems != null) {
            cartItems.remove(item);
            item.setCart(null);
        }
    }
}
