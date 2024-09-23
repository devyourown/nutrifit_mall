package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addItemToCart(User user, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저가 카트가 없습니다."));

        if (cart == null) {
            cart = Cart.builder().build();
            cartRepository.save(cart);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);
        if (cartItem == null) {
            cartItem = CartItem.builder().cart(cart).product(product).quantity(quantity).imageUrl(product.getImageUrls().get(0)).build();
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cart.addCartItem(cartItem);
        cartItemRepository.save(cartItem);
    }

    public List<CartItemDto> getCartItems(User user) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저가 카트가 없습니다."));
        return cartItemRepository.findByCart(cart);
    }

    @Transactional
    public void removeItemFromCart(User user, Long productId) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저가 카트가 없습니다."));

        if (cart != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
            CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                    .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품이 존재하지 않습니다."));

            cartItemRepository.delete(cartItem);
            cart.removeCartItem(cartItem);
        } else {
            throw new IllegalArgumentException("장바구니가 존재하지 않습니다.");
        }
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저가 카트가 없습니다."));

        if (cart != null && !cart.getCartItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
        } else {
            throw new IllegalArgumentException("장바구니가 존재하지 않습니다.");
        }
    }

    @Transactional
    public void updateItemQuantity(User user, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저가 카트가 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 카트에 존재하지 않습니다."));
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cart.removeCartItem(cartItem);
            cartItem.setQuantity(quantity);
            cart.addCartItem(cartItem);
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void updateCart(User user, List<CartItemDto> items) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저가 카트가 없습니다."));
        cart.getCartItems().clear();

        List<Long> productIds = items.stream().map(CartItemDto::getId).toList();

        List<Product> products = productRepository.findByIdIn(productIds);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        for (CartItemDto itemDto : items) {
            Product product = productMap.get(itemDto.getId());
            if (product == null) {
                throw new IllegalArgumentException("상품을 찾을 수 없습니다: " + itemDto.getId());
            }
            CartItem cartItem = CartItem.builder()
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .cart(cart)
                    .imageUrl(itemDto.getImageUrl())
                    .build();

            cart.addCartItem(cartItem);
        }

        cartRepository.save(cart);
    }
}
