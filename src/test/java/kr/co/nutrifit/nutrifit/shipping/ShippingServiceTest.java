package kr.co.nutrifit.nutrifit.shipping;

import kr.co.nutrifit.nutrifit.backend.dto.OrdererDto;
import kr.co.nutrifit.nutrifit.backend.dto.ShippingStatusDto;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ShippingRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import kr.co.nutrifit.nutrifit.backend.services.ShippingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShippingServiceTest {

    @InjectMocks
    private ShippingService shippingService;

    @Mock
    private ShippingRepository shippingRepository;

    @Mock
    private OrderRepository orderRepository;

    private User user;
    private Order order;
    private Shipping shipping;
    private ShippingStatusDto shippingStatusDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("testuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        order = Order.builder()
                .user(user)
                .totalAmount(10000L)
                .orderDate(LocalDateTime.now())
                .build();

        shipping = Shipping.builder()
                .id(1L)
                .order(order)
                .recipientName("John Doe")
                .address("123 Main St")
                .build();

        shippingStatusDto = ShippingStatusDto.builder()
                .shippingId(shipping.getId())
                .build();
    }

    @Test
    void updateShippingStatus_ShouldUpdateShipping() {
        when(shippingRepository.findById(shipping.getId())).thenReturn(Optional.of(shipping));
        when(shippingRepository.save(any(Shipping.class))).thenReturn(shipping);

        OrdererDto updatedShipping = shippingService.updateShippingStatus(shippingStatusDto);

        verify(shippingRepository, times(1)).save(shipping);
    }

    @Test
    void updateShippingStatusBulk_ShouldUpdateMultipleShippings() {
        List<Shipping> shippings = List.of(shipping);
        List<ShippingStatusDto> dtos = List.of(shippingStatusDto);

        when(shippingRepository.findAllById(anyList())).thenReturn(shippings);
        when(shippingRepository.saveAll(anyList())).thenReturn(shippings);

        List<OrdererDto> updatedShippings = shippingService.updateShippingStatusBulk(dtos);

        assertEquals(1, updatedShippings.size());
        verify(shippingRepository, times(1)).saveAll(shippings);
    }

    @Test
    void getShippingByOrderId_ShouldReturnShipping() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(shippingRepository.findByOrderId(order.getId())).thenReturn(Optional.of(shipping));
    }
}
