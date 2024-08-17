package kr.co.nutrifit.nutrifit.cart;

import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.persistence.CartItemRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.CartRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Cart;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.CartItem;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.services.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @Mock
    private User user;

    @Mock
    private Cart cart;

    @Mock
    private Product product;

    @Mock
    private CartItem cartItem;

    @Test
    void addItemToCart_ShouldAddNewItem() {
        // Given
        Long productId = 1L;
        int quantity = 2;
        when(user.getCart()).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        // When
        cartService.addItemToCart(user, productId, quantity);

        // Then
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addItemToCart_ShouldIncreaseQuantityOfExistingItem() {
        // Given
        Long productId = 1L;
        int quantity = 2;
        when(user.getCart()).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));

        // When
        cartService.addItemToCart(user, productId, quantity);

        // Then
        verify(cartItem).setQuantity(cartItem.getQuantity() + quantity);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void getCartItems_ShouldReturnEmptyListWhenCartIsEmpty() {
        // Given
        when(user.getCart()).thenReturn(cart);
        when(cart.getCartItems()).thenReturn(List.of());

        // When
        List<CartItemDto> result = cartService.getCartItems(user);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void removeItemFromCart_ShouldRemoveItem() {
        // Given
        Long productId = 1L;
        when(user.getCart()).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));

        // When
        cartService.removeItemFromCart(user, productId);

        // Then
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void clearCart_ShouldClearAllItems() {
        // Given
        when(user.getCart()).thenReturn(cart);
        when(cart.getCartItems()).thenReturn(List.of(cartItem));

        // When
        cartService.clearCart(user);

        // Then
        verify(cartItemRepository).deleteAll(cart.getCartItems());
    }

    @Test
    void updateItemQuantity_ShouldUpdateQuantity() {
        // Given
        Long productId = 1L;
        int newQuantity = 3;
        when(user.getCart()).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));

        // When
        cartService.updateItemQuantity(user, productId, newQuantity);

        // Then
        verify(cartItem).setQuantity(newQuantity);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void updateItemQuantity_ShouldRemoveItemIfQuantityIsZero() {
        // Given
        Long productId = 1L;
        int newQuantity = 0;
        when(user.getCart()).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));

        // When
        cartService.updateItemQuantity(user, productId, newQuantity);

        // Then
        verify(cartItemRepository).delete(cartItem);
    }
}

