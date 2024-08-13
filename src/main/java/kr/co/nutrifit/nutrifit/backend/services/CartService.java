package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.persistence.CartItemRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.CartRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Cart;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.CartItem;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addItemToCart(String username, Long productId, int quantity) {
        User user = userRepository.findByUsername(username);
        Cart cart = user.getCart();

        if (cart == null) {
            cart = Cart.builder()
                    .user(user).build();
            cartRepository.save(cart);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity).build();
        cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCartItems(String username) {
        User user = userRepository.findByUsername(username);
        Cart cart = user.getCart();
        return cart.getCartItems();
    }

    @Transactional
    public void removeItemFromCart(String username, Long productId) {
        User user = userRepository.findByUsername(username);
        Cart cart = user.getCart();

        if (cart != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
            CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                    .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품이 존재하지 않습니다."));

            cartItemRepository.delete(cartItem);
        } else {
            throw new IllegalArgumentException("장바구니가 존재하지 않습니다.");
        }
    }

    @Transactional
    public void clearCart(String username) {
        User user = userRepository.findByUsername(username);
        Cart cart = user.getCart();

        if (cart != null) {
            cartItemRepository.deleteAll(cart.getCartItems());
        } else {
            throw new IllegalArgumentException("장바구니가 존재하지 않습니다.");
        }
    }

    @Transactional
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장바구니가 존재하지 않습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 카트에 존재하지 않습니다."));
        if (quantity < 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }
}
