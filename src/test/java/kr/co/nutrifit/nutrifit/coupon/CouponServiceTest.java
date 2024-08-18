package kr.co.nutrifit.nutrifit.coupon;

import kr.co.nutrifit.nutrifit.backend.dto.CouponDto;
import kr.co.nutrifit.nutrifit.backend.persistence.CouponRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserCouponRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import kr.co.nutrifit.nutrifit.backend.services.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Coupon coupon;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("testuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        coupon = Coupon.builder()
                .id(1L)
                .code("TESTCOUPON")
                .description("Test Coupon")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(10)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validUntil(LocalDateTime.now().plusDays(7))
                .minimumOrderAmount(5000)
                .maxDiscountAmount(1000)
                .isActive(true)
                .remainingQuantity(10)
                .build();
    }

    @Test
    void createCoupon_ShouldSaveCoupon() {
        CouponDto couponDto = CouponDto.builder()
                .code("TESTCOUPON")
                .description("Test Coupon")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(10)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validUntil(LocalDateTime.now().plusDays(7))
                .minimumOrderAmount(5000)
                .maxDiscountAmount(1000)
                .remainingQuantity(10)
                .build();

        couponService.createCoupon(couponDto);

        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void assignCouponToUser_ShouldAssignCoupon() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(couponRepository.findByCode("TESTCOUPON")).thenReturn(Optional.of(coupon));
        when(userCouponRepository.existsByUserAndCoupon(user, coupon)).thenReturn(false);

        couponService.assignCouponToUser("TESTCOUPON", 1L);

        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void assignCouponToUser_ShouldThrowExceptionIfCouponAlreadyAssigned() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(couponRepository.findByCode("TESTCOUPON")).thenReturn(Optional.of(coupon));
        when(userCouponRepository.existsByUserAndCoupon(user, coupon)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> couponService.assignCouponToUser("TESTCOUPON", 1L));

        verify(userCouponRepository, never()).save(any(UserCoupon.class));
    }

    @Test
    void useCoupon_ShouldMarkCouponAsUsed() {
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setUsed(false);

        when(userCouponRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(userCoupon));

        couponService.useCoupon(1L, 1L, 10000L);

        assertTrue(userCoupon.isUsed());
        verify(userCouponRepository, times(1)).save(userCoupon);
    }

    @Test
    void useCoupon_ShouldThrowExceptionIfCouponIsExpired() {
        coupon.setValidUntil(LocalDateTime.now().minusDays(1)); // 만료된 쿠폰

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setUsed(false);

        when(userCouponRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(userCoupon));

        assertThrows(IllegalStateException.class, () -> couponService.useCoupon(1L, 1L, 10000L));

        verify(userCouponRepository, never()).save(userCoupon);
    }
}
