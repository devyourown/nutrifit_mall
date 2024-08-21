package kr.co.nutrifit.nutrifit.order;

import kr.co.nutrifit.nutrifit.backend.controllers.OrderController;
import kr.co.nutrifit.nutrifit.backend.dto.OrderDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import kr.co.nutrifit.nutrifit.backend.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private User user;
    private Product product;
    private Order order;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        user = userRepository.save(user);

        product = Product.builder()
                .name("Product 1")
                .description("Description")
                .discountedPrice(1000L)
                .stockQuantity(10)
                .build();

        product = productRepository.save(product);

        order = Order.builder()
                .user(user)
                .totalAmount(2000L)
                .orderDate(LocalDateTime.now())
                .build();

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .price(product.getDiscountedPrice())
                .quantity(2)
                .totalAmount(product.getDiscountedPrice() * 2)
                .build();

        order.setOrderItems(List.of(orderItem));

        order = orderRepository.save(order);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        // Given
        OrderItemDto orderItemDto = OrderItemDto.builder()
                .productId(product.getId())
                .quantity(2)
                .build();

        // When
        Order createdOrder = orderService.createOrder(user, List.of(orderItemDto));

        // Then
        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getOrderItems().size());
        assertEquals(product.getId(), createdOrder.getOrderItems().get(0).getProduct().getId());
        assertEquals(2000L, createdOrder.getTotalAmount());
    }

    @Test
    void getOrdersByUser_ShouldReturnListOfOrders() {
        OrderItemDto orderItemDto = OrderItemDto.builder()
                .productId(product.getId())
                .quantity(2)
                .build();
        Order createdOrder = orderService.createOrder(user, List.of(orderItemDto));
        // When
        List<OrderDto> orders = orderService.getOrdersByUser(user);

        // Then
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals(2000L, orders.get(1).getTotalAmount());
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductNotFound() {
        // Given
        OrderItemDto orderItemDto = OrderItemDto.builder()
                .productId(3000L)// non-existing product ID
                .quantity(2)
                .build();

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            orderService.createOrder(user, List.of(orderItemDto));
        });
    }
}

