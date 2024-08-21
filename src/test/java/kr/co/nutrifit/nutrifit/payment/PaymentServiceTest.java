package kr.co.nutrifit.nutrifit.payment;

import kr.co.nutrifit.nutrifit.backend.dto.PaymentDto;
import kr.co.nutrifit.nutrifit.backend.persistence.*;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import kr.co.nutrifit.nutrifit.backend.services.*;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto;
import kr.co.nutrifit.nutrifit.backend.dto.ShippingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaymentServiceTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private OrderItemRepository itemRepository;

    @Autowired
    private ProductRepository productRepository;


    private User user;
    private PaymentDto paymentDto;
    private Payment payment;
    private Order order;
    private Shipping shipping;

    @BeforeEach
    void setUp() {

        // 사용자 및 주문 관련 기본 설정
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

        paymentDto = PaymentDto.builder()
                .orderId(order.getId())
                .amount(10000L)
                .paymentMethod("card")
                .impUid("test_imp_uid")
                .usedPoints(1000)
                .orderItems(List.of(new OrderItemDto(1L, 2, 2000L, "product1", "imageUrl")))
                .shippingDto(ShippingDto.builder()
                        .recipientName("John Doe")
                        .address("123 Main St")
                        .phoneNumber("010-1234-5678")
                        .build())
                .build();

        payment = Payment.builder()
                .order(order)
                .user(user)
                .amount(10000L)
                .impUid("test_imp_uid")
                .paymentMethod("card")
                .paymentDate(LocalDateTime.now())
                .paymentStatus("pending")
                .build();
        payment = paymentRepository.save(payment);

        shipping = Shipping.builder()
                .order(order)
                .orderDate(LocalDateTime.now())
                .shippingStatus(ShippingStatus.ORDERED)
                .address(paymentDto.getShippingDto().getAddress())
                .phoneNumber(paymentDto.getShippingDto().getPhoneNumber())
                .recipientName(paymentDto.getShippingDto().getRecipientName())
                .build();

        order.setShipping(shipping);

        order = orderRepository.save(order);
        shipping = shippingRepository.save(shipping);
    }

    @Test
    void getPaymentByIdAndUser_ShouldReturnPaymentDto() {
        PaymentDto result = paymentService.getPaymentByIdAndUser(payment.getId(), user);

        assertNotNull(result);
        assertEquals("test_imp_uid", result.getImpUid());
    }

    @Test
    void getPaymentsByUser_ShouldReturnListOfPaymentDto() {

        List<PaymentDto> results = paymentService.getPaymentsByUser(user);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("test_imp_uid", results.get(0).getImpUid());

    }
}
