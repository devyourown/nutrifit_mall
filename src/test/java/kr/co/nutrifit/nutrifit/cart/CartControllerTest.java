package kr.co.nutrifit.nutrifit.cart;

import kr.co.nutrifit.nutrifit.backend.controllers.CartController;
import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.persistence.CartItemRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.CartRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import kr.co.nutrifit.nutrifit.backend.security.CustomUserDetailsService;
import kr.co.nutrifit.nutrifit.backend.security.JwtTokenProvider;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.CartService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Mock
    private CartService cartService;  // CartService를 Mocking

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @InjectMocks
    private CartController cartController;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private UserAdapter userAdapter;

    @BeforeEach
    public void setUp() {
        // Mock User, Cart, Product, CartItem 생성
        user = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        product = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Description")
                .discountedPrice(10000L)
                .stockQuantity(10)
                .imageUrls(new ArrayList<>())
                .build();

        product.setId(productRepository.save(product).getId());

        cart = Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        cart.addCartItem(cartItem);
        cartRepository.save(cart);
        cartItemRepository.save(cartItem);
        user.setCart(cart);

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        userAdapter = new UserAdapter(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userAdapter, null, userAdapter.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void addItemToCart_ShouldReturn201Status() throws Exception {
        mockMvc.perform(post("/api/cart/items")  // Mock JWT 토큰을 헤더에 포함
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .param("productId", String.valueOf(product.getId()))  // 임의의 Product ID 사용
                        .param("quantity", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("상품이 장바구니에 추가되었습니다."));
    }

    @Test
    void getCartItems_ShouldReturnCartItems() throws Exception {
        Assert.assertNotNull(userAdapter.getUser());
        Assert.assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/cart/items")
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                                .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Product 1"));
    }

    @Test
    void updateCartItemQuantity_ShouldReturn200Status() throws Exception {
        mockMvc.perform(put("/api/cart/items") // Mock JWT 토큰을 헤더에 포함
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .param("productId", String.valueOf(product.getId()))  // 임의의 Product ID 사용
                        .param("quantity", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("아이템 수량이 변경되었습니다."));
    }

    @Test
    void removeItemFromCart_ShouldReturn204Status() throws Exception {
        mockMvc.perform(delete("/api/cart/items/{productId}", product.getId())  // Mock JWT 토큰을 헤더에 포함
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void clearCart_ShouldReturn204Status() throws Exception {
        mockMvc.perform(delete("/api/cart/items")  // Mock JWT 토큰을 헤더에 포함
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
