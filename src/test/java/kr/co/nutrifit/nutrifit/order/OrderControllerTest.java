package kr.co.nutrifit.nutrifit.order;


import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.nutrifit.nutrifit.backend.controllers.OrderController;
import kr.co.nutrifit.nutrifit.backend.dto.OrderDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemExcelDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserAdapter userAdapter;

    @Test
    @WithMockUser(roles = "USER")
    void getUserOrders_shouldReturnOrdersForUser() throws Exception {
        // Given
        User user = new User();
        when(userAdapter.getUser()).thenReturn(user);
        List<OrderDto> orderList = List.of(new OrderDto());

        // When & Then
        mockMvc.perform(get("/api/orders")
                        .with(user(userAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrders_shouldReturnOrdersForAdmin() throws Exception {
        // Given
        when(userAdapter.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())));
        Page<OrderDto> orders = new PageImpl<>(List.of(new OrderDto()));
        when(orderService.getOrders(any(Pageable.class))).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/orders/admin")
                .with(user(userAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrders_whenNotAdmin_shouldReturnForbidden() throws Exception {
        // Non-admin user should get forbidden error

        mockMvc.perform(get("/api/orders/admin")
                .with(user(userAdapter)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrdersByFilter_shouldReturnFilteredOrders() throws Exception {
        // Given
        when(userAdapter.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())));
        Page<OrderDto> orders = new PageImpl<>(List.of(new OrderDto()));
        when(orderService.getOrdersByFilter(anyString(), any(Pageable.class))).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/api/orders/admin/filter")
                        .param("status", "SHIPPED")
                        .with(user(userAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrdersForExcelByFilter_shouldReturnExcelOrders() throws Exception {
        // Given
        when(userAdapter.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())));
        List<OrderItemExcelDto> excelOrders = List.of(new OrderItemExcelDto());
        when(orderService.getOrdersForExcelByFilter(anyString())).thenReturn(excelOrders);

        // When & Then
        mockMvc.perform(get("/api/orders/excel/filter")
                        .param("status", "SHIPPED")
                .with(user(userAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTrackingNumber_shouldReturnSuccess() throws Exception {
        // Given
        List<OrderItemExcelDto> dto = List.of(new OrderItemExcelDto());
        doNothing().when(orderService).updateTrackingNumbers(dto);

        // When & Then
        mockMvc.perform(put("/api/orders/tracking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .with(user(userAdapter)))
                .andExpect(status().isOk())
                .andExpect(content().string("성공적으로 운송장번호가 업데이트 되었습니다."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTrackingNumber_shouldReturnInternalServerErrorOnFailure() throws Exception {
        // Given
        List<OrderItemExcelDto> dto = List.of(new OrderItemExcelDto());
        doThrow(new RuntimeException("Error updating tracking numbers")).when(orderService).updateTrackingNumbers(dto);

        // When & Then
        mockMvc.perform(put("/api/orders/tracking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                .with(user(userAdapter)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("운송장번호 업데이트에 실패했습니다."));
    }
}
