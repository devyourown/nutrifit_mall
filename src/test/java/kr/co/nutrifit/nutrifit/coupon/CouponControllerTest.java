package kr.co.nutrifit.nutrifit.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.nutrifit.nutrifit.backend.controllers.CouponController;
import kr.co.nutrifit.nutrifit.backend.dto.CouponDto;
import kr.co.nutrifit.nutrifit.backend.persistence.CouponRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Coupon;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.DiscountType;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @MockBean
    private UserAdapter userAdapter;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCoupon_whenAdmin_shouldReturnCreated() throws Exception {
        CouponDto couponDto = new CouponDto();
        couponDto.setCode("TEST123");

        mockMvc.perform(post("/api/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(couponDto)))
                .andExpect(status().isCreated());

        verify(couponService, times(1)).createCoupon(any(CouponDto.class));
    }

    @Test
    @WithMockUser
    void createCoupon_whenNotAdmin_shouldReturnForbidden() throws Exception {
        CouponDto couponDto = new CouponDto();
        couponDto.setCode("TEST123");

        mockMvc.perform(post("/api/coupon/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(couponDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void assignCouponToUser_shouldReturnNoContent() throws Exception {
        String couponCode = "TEST123";
        when(userAdapter.getUser()).thenReturn(new User());

        mockMvc.perform(post("/api/coupon/assign")
                        .param("code", couponCode)
                        .with(user(userAdapter)))
                .andExpect(status().isNoContent());

    }

    @Test
    void getUserCoupon_shouldReturnCoupons() throws Exception {
        List<CouponDto> coupons = List.of(
                new CouponDto("TEST123", "Discount 10%", 10, DiscountType.PERCENTAGE, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),10, 10, 10)
        );
        when(userAdapter.getUser()).thenReturn(new User());

        mockMvc.perform(get("/api/coupon")
                        .with(user(userAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("TEST123"));

    }
}

