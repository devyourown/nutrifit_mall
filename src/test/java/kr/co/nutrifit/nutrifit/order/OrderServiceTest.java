package kr.co.nutrifit.nutrifit.order;

import kr.co.nutrifit.nutrifit.backend.controllers.OrderController;
import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrderDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemExcelDto;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderItemRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import kr.co.nutrifit.nutrifit.backend.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    void createOrder_shouldCreateAndSaveOrder() {
        // Given
        User user = new User();
        List<CartItemDto> cartItems = List.of(
                CartItemDto.builder().id(1L).quantity(2).build()
        );
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .discountedPrice(100L)
                .build();

    }

    @Test
    void createOrder_whenProductNotFound_shouldThrowException() {
        // Given
        List<CartItemDto> cartItems = List.of(CartItemDto.builder().id(999L).quantity(1).build());

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());


    }

    @Test
    void getOrdersByUser_shouldReturnUserOrders() {
        // Given
        User user = new User();

        // Create a ProductDetail object to associate with Product
        ProductDetail productDetail = ProductDetail.builder()
                .build();


    }

    @Test
    void updateTrackingNumbers_shouldUpdateTrackingNumbersCorrectly() {
        // Given: 입력 데이터(OrderItemExcelDto 리스트)
        List<OrderItemExcelDto> dtoList = List.of(
                new OrderItemExcelDto("ORDER123", "John Doe", "123456", "Jane Doe", "654321",
                        "123 Main St", "Apt 101", "Leave at door", "Product1", 2, "TRACK123")
        );

        // 찾을 OrderItem 생성
        Order order = Order
                .builder()
                .orderPaymentId("ORDER123")
                .build();
        Product product = Product.builder().name("Product1").build();


        // ArgumentCaptor to capture the saved OrderItems
        ArgumentCaptor<List<OrderItem>> captor = ArgumentCaptor.forClass(List.class);

        // When: updateTrackingNumbers 메소드 실행
        orderService.updateTrackingNumbers(dtoList);

        // Then: OrderItem의 trackingNumber가 제대로 설정되었는지 확인
        verify(orderItemRepository, times(1)).saveAll(captor.capture()); // saveAll 호출 확인 및 데이터 캡처

        List<OrderItem> savedItems = captor.getValue(); // 캡처된 저장된 항목들
        assertEquals(1, savedItems.size()); // 저장된 항목 수 확인
        assertEquals("TRACK123", savedItems.get(0).getTrackingNumber()); // trackingNumber가 올바르게 설정되었는지 확인
    }


    @Test
    void getOrdersByFilter_shouldReturnFilteredOrders() {
        // Given
        Page<OrderDto> orders = new PageImpl<>(List.of(new OrderDto()));
        when(orderItemRepository.findAllByShippingStatusAndPage(anyString(), any(Pageable.class)))
                .thenReturn(orders);

        // When
        Page<OrderDto> result = orderService.getOrdersByFilter("SHIPPED", Pageable.unpaged());

        // Then
        assertEquals(1, result.getTotalElements());
        verify(orderItemRepository, times(1)).findAllByShippingStatusAndPage(anyString(), any(Pageable.class));
    }

    @Test
    void getOrdersForExcelByFilter_shouldReturnExcelOrders() {
        // Given
        List<OrderItemExcelDto> excelOrders = List.of(new OrderItemExcelDto());

        when(orderItemRepository.findOrderItemsByStatus(anyString())).thenReturn(excelOrders);

        // When
        List<OrderItemExcelDto> result = orderService.getOrdersForExcelByFilter("SHIPPED");

        // Then
        assertEquals(1, result.size());
        verify(orderItemRepository, times(1)).findOrderItemsByStatus(anyString());
    }
}
