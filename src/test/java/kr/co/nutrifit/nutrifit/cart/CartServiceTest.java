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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void addItemToCart_whenProductExists_shouldAddNewItem() {
        // Given
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .originalPrice(1000L)
                .discountedPrice(900L)
                .stockQuantity(100)
                .imageUrls(List.of("image1.jpg"))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        // When
        cartService.addItemToCart(user, 1L, 2);

        // Then
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        assertEquals(1, cart.getCartItems().size());
        CartItem addedItem = cart.getCartItems().get(0);
        assertEquals(2, addedItem.getQuantity());
        assertEquals(product, addedItem.getProduct());
    }

    @Test
    void addItemToCart_whenProductNotFound_shouldThrowException() {
        // Given
        User user = new User();

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cartService.addItemToCart(user, 1L, 2));
    }

    @Test
    void removeItemFromCart_whenItemExists_shouldRemoveItem() {
        // Given
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();
        CartItem cartItem = new CartItem(1L, cart, product, 2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));

        // When
        cartService.removeItemFromCart(user, 1L);

        // Then
        verify(cartItemRepository, times(1)).delete(cartItem);
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    void removeItemFromCart_whenItemDoesNotExist_shouldThrowException() {
        // Given
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cartService.removeItemFromCart(user, 1L));
    }

    @Test
    void updateItemQuantity_whenItemExists_shouldUpdateQuantity() {
        // Given
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .stockQuantity(100)
                .build();
        CartItem cartItem = new CartItem(1L, cart, product, 2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(cartItem));

        // When
        cartService.updateItemQuantity(user, 1L, 5);

        // Then
        verify(cartItemRepository, times(1)).save(cartItem);
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    void updateItemQuantity_whenProductNotFound_shouldThrowException() {
        // Given
        User user = new User();

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cartService.updateItemQuantity(user, 1L, 5));
    }

    @Test
    void updateItemQuantity_whenItemDoesNotExist_shouldThrowException() {
        // Given
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cartService.updateItemQuantity(user, 1L, 5));
    }

    @Test
    void clearCart_shouldClearAllItems() {
        // Given
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();
        CartItem cartItem = new CartItem(1L, cart, product, 2);

        cart.addCartItem(cartItem);

        // When
        cartService.clearCart(user);

        // Then
        verify(cartItemRepository, times(1)).deleteAll(cart.getCartItems());
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    void updateCart_whenItemsAreValid_shouldUpdateCart() {
        // Given
        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        List<CartItemDto> items = List.of(new CartItemDto(1L, "Product1", "Description1", 1000L, "image1.jpg", 2));

        Product product = Product.builder()
                .id(1L)
                .name("Product1")
                .build();
        List<Product> products = List.of(product);

        when(productRepository.findByIdIn(anyList())).thenReturn(products);

        // When
        cartService.updateCart(user, items);

        // Then
        verify(cartRepository, times(1)).save(cart);
        assertEquals(1, cart.getCartItems().size());
        assertEquals(2, cart.getCartItems().get(0).getQuantity());
        assertEquals(product, cart.getCartItems().get(0).getProduct());
    }
}



