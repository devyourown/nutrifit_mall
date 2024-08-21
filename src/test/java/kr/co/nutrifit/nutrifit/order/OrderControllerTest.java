package kr.co.nutrifit.nutrifit.order;


import kr.co.nutrifit.nutrifit.backend.controllers.OrderController;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderItemRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository itemRepository;

    private User user;
    private UserAdapter userAdapter;
    private Order order;

    @BeforeEach
    public void setUp() {
        // Mock User, UserAdapter, and Order setup
        user = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        user = userRepository.save(user);

        Product product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .discountedPrice(1000L)
                .stockQuantity(100)
                .category("Test Category")
                .build();

        product = productRepository.save(product);

        order = Order.builder()
                .user(user)
                .totalAmount(10000L)
                .orderDate(LocalDateTime.now())
                .build();

        order = orderRepository.save(order);

        OrderItem orderItem = OrderItem
                .builder()
                .product(product)
                .price(1000L)
                .quantity(1)
                .order(order)
                .totalAmount(1000L)
                .build();

        order.addOrderItem(orderItem);
        itemRepository.save(orderItem);

        user.addOrder(order);

        userAdapter = new UserAdapter(user);
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userAdapter, null, userAdapter.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getUserOrders_ShouldReturnOrders() throws Exception {
        mockMvc.perform(get("/orders")
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order.getId()))
                .andExpect(jsonPath("$[0].totalAmount").value(order.getTotalAmount()));
    }
}
