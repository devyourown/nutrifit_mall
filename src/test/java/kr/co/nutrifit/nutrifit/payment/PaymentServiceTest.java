package kr.co.nutrifit.nutrifit.payment;

import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrdererDto;
import kr.co.nutrifit.nutrifit.backend.dto.PaymentDto;
import kr.co.nutrifit.nutrifit.backend.persistence.*;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import kr.co.nutrifit.nutrifit.backend.services.*;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto;
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
import java.util.Arrays;
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
    }

    @Test
    void getPaymentByIdAndUser_ShouldReturnPaymentDto() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .password("password123")
                .isOAuth(false)
                .role(Role.ROLE_USER)
                .imageUrl("http://example.com/image.jpg")
                .address("서울시 강남구")
                .addressDetails("아파트 101동 1010호")
                .shippingDetails("문 앞에 놓아주세요")
                .build();

        User savedUser = userRepository.save(user);

        Product fakeProduct = Product.builder()
                .id(18L)
                .name("스마트폰 X")
                .description("최신형 스마트폰 X 모델입니다.")
                .originalPrice(1000000L)
                .discountedPrice(900000L)
                .stockQuantity(100)
                .lowStockThreshold(10)
                .imageUrls(Arrays.asList(
                        "http://example.com/smartphone_x_1.jpg",
                        "http://example.com/smartphone_x_2.jpg"
                ))
                .category("전자기기")
                .badgeTexts(Arrays.asList("신제품", "할인중"))
                .reviewRating(45L)  // 4.5점을 의미 (10을 곱한 값)
                .reviewCount(120L)
                .build();

        Product fakeProduct2 = Product.builder()
                .id(19L)
                .name("스마트폰 X2")
                .description("최신형 스마트폰 X 모델입니다.")
                .originalPrice(1000000L)
                .discountedPrice(900000L)
                .stockQuantity(100)
                .lowStockThreshold(10)
                .imageUrls(Arrays.asList(
                        "http://example.com/smartphone_x_1.jpg",
                        "http://example.com/smartphone_x_2.jpg"
                ))
                .category("전자기기")
                .badgeTexts(Arrays.asList("신제품", "할인중"))
                .reviewRating(45L)  // 4.5점을 의미 (10을 곱한 값)
                .reviewCount(120L)
                .build();
        Product product = productRepository.save(fakeProduct);
        Product product2 = productRepository.save(fakeProduct2);
// CartItemDto 리스트 생성
        List<CartItemDto> cartItems = Arrays.asList(
                CartItemDto.builder()
                        .id(product.getId())
                        .name("상품1")
                        .description("상품1 설명")
                        .price(10000L)
                        .imageUrl("http://example.com/item1.jpg")
                        .quantity(2)
                        .build(),
                CartItemDto.builder()
                        .id(product2.getId())
                        .name("상품2")
                        .description("상품2 설명")
                        .price(15000L)
                        .imageUrl("http://example.com/item2.jpg")
                        .quantity(1)
                        .build()
        );

// OrdererDto 객체 생성
        OrdererDto orderer = OrdererDto.builder()
                .recipientName("홍길동")
                .recipientPhone("010-1234-5678")
                .ordererName("김철수")
                .ordererPhone("010-9876-5432")
                .address("서울시 강남구")
                .addressDetail("아파트 101동 1010호")
                .cautions("부재시 경비실에 맡겨주세요")
                .build();

// PaymentDto 객체 생성
        PaymentDto paymentDto = PaymentDto.builder()
                .orderId("ORDER-12345")
                .total(35000L)
                .subtotal(35000L)
                .discount(0L)
                .shippingFee(2500L)
                .paymentMethod("신용카드")
                .cartItems(cartItems)
                .ordererDto(orderer)
                .paymentDate(LocalDateTime.now())
                .couponId(null)
                .usedPoints(0)
                .build();

// status 문자열
        String status = "PAID";

// 테스트 실행
        paymentService.createOrderAndPaymentAndShipping(savedUser, paymentDto, status);
    }

    @Test
    void testCreateOrderAndPaymentAndShipping() {

    }

    @Test
    void getPaymentsByUser_ShouldReturnListOfPaymentDto() {

        List<PaymentDto> results = paymentService.getPaymentsByUser(user);

        assertNotNull(results);
        assertEquals(1, results.size());

    }
}
