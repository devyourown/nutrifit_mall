package kr.co.nutrifit.nutrifit.shipping;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.nutrifit.nutrifit.backend.controllers.ShippingController;
import kr.co.nutrifit.nutrifit.backend.dto.ShippingStatusDto;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ShippingRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.ShippingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ShippingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShippingService shippingService;

    @InjectMocks
    private ShippingController shippingController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private User admin;
    private Order order;
    private Shipping shipping;
    private ShippingStatusDto shippingStatusDto;
    private UserAdapter userAdapter;
    private UserAdapter adminAdapter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingRepository shippingRepository;

    @BeforeEach
    public void setUp() {

        user = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        admin = User.builder()
                .username("adminuser")
                .email("adminuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_ADMIN)
                .build();

        user = userRepository.save(user);
        admin = userRepository.save(admin);

        order = Order.builder()
                .user(user)
                .totalAmount(10000L)
                .orderDate(LocalDateTime.now())
                .build();

        order = orderRepository.save(order);

        shipping = Shipping.builder()
                .order(order)
                .recipientName("John Doe")
                .address("123 Main St")
                .phoneNumber("010-1234-5678")
                .shippingStatus(ShippingStatus.ORDERED)
                .orderDate(LocalDateTime.now())
                .build();

        shipping = shippingRepository.save(shipping);

        shippingStatusDto = ShippingStatusDto.builder()
                .shippingId(shipping.getId())
                .status(ShippingStatus.SHIPPED)
                .build();

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        userAdapter = new UserAdapter(user);
        adminAdapter = new UserAdapter(admin);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userAdapter, null, userAdapter.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void updateShippingStatus_ShouldReturnOk_ForAdmin() throws Exception {
        mockMvc.perform(put("/api/shipping")
                        .with(SecurityMockMvcRequestPostProcessors.user(adminAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shippingStatusDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shipping.getId()));
    }

    @Test
    void updateShippingStatus_ShouldReturnForbidden_ForNonAdmin() throws Exception {
        mockMvc.perform(put("/api/shipping")
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shippingStatusDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateShippingStatusBulk_ShouldReturnOk_ForAdmin() throws Exception {
        List<ShippingStatusDto> dtos = new ArrayList<>();
        dtos.add(shippingStatusDto);
        mockMvc.perform(put("/api/shipping/bulk")
                        .with(SecurityMockMvcRequestPostProcessors.user(adminAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(shipping.getId()));
    }

    @Test
    void updateShippingStatusBulk_ShouldReturnForbidden_ForNonAdmin() throws Exception {
        List<ShippingStatusDto> dtos = new ArrayList<>();
        dtos.add(shippingStatusDto);
        mockMvc.perform(put("/api/shipping/bulk")
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtos)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrderShipping_ShouldReturnShipping_ForUser() throws Exception {
        mockMvc.perform(get("/api/shipping/{orderId}", order.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shipping.getId()));
    }
}

