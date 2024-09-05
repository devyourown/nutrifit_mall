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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void createCoupon_shouldSaveCoupon() {
        // Given
        CouponDto couponDto = CouponDto.builder()
                .code("TEST123")
                .description("Test Discount")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(10)
                .validFrom(LocalDateTime.now())
                .validUntil(LocalDateTime.now().plusDays(10))
                .remainingQuantity(100)
                .build();

        // When
        couponService.createCoupon(couponDto);

        // Then
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void assignCouponToUser_shouldAssignCouponWhenValid() {
        // Given
        User user = new User();
        user.setId(1L);

        Coupon coupon = Coupon.builder()
                .code("TEST123")
                .remainingQuantity(10)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validUntil(LocalDateTime.now().plusDays(1))
                .isActive(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(couponRepository.findByCode("TEST123")).thenReturn(Optional.of(coupon));
        when(userCouponRepository.existsByUserAndCoupon(user, coupon)).thenReturn(false);

        // When
        couponService.assignCouponToUser("TEST123", 1L);

        // Then
        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
        verify(couponRepository, times(1)).save(coupon);
        assertEquals(9, coupon.getRemainingQuantity());
    }

    @Test
    void assignCouponToUser_shouldThrowExceptionWhenCouponInvalid() {

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> couponService.assignCouponToUser("INVALID", 1L));
    }

    @Test
    void useCoupon_whenValid_shouldMarkCouponAsUsed() {
        // Given
        User user = new User();
        user.setId(1L);

        Coupon coupon = Coupon.builder()
                .code("TEST123")
                .minimumOrderAmount(1000)
                .validUntil(LocalDateTime.now().plusDays(1))
                .build();

        UserCoupon userCoupon = UserCoupon.builder()
                .coupon(coupon)
                .user(user)
                .build();

        when(userCouponRepository.findByIdAndUser(anyLong(), eq(user)))
                .thenReturn(Optional.of(userCoupon));

        // When
        couponService.useCoupon(user, 1L, 2000L);

        // Then
        verify(userCouponRepository, times(1)).save(userCoupon);
        assertTrue(userCoupon.isUsed());
    }

    @Test
    void useCoupon_whenCouponExpired_shouldThrowException() {
        // Given
        User user = new User();

        Coupon coupon = Coupon.builder()
                .code("TEST123")
                .validUntil(LocalDateTime.now().minusDays(1))
                .build();

        UserCoupon userCoupon = UserCoupon.builder()
                .coupon(coupon)
                .user(user)
                .build();

        when(userCouponRepository.findByIdAndUser(anyLong(), eq(user)))
                .thenReturn(Optional.of(userCoupon));

        // When & Then
        assertThrows(IllegalStateException.class, () -> couponService.useCoupon(user, 1L, 2000L));
    }

    @Test
    void getUserCoupon_shouldReturnListOfCoupons() {
        // Given
        User user = new User();
        Coupon coupon = Coupon.builder()
                .code("TEST123")
                .description("Discount 10%")
                .discountValue(10)
                .build();

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);

        when(userCouponRepository.findAllByUser(user)).thenReturn(List.of(userCoupon));

        // When
        List<CouponDto> coupons = couponService.getUserCoupon(user);

        // 중간 확인: CouponDto 변환 과정 확인
        assertNotNull(coupons);  // null 체크
        assertFalse(coupons.isEmpty());  // 비어 있지 않음 체크
        System.out.println(coupons);

        // Then
        assertEquals(1, coupons.size());
        assertEquals("Discount 10%", coupons.get(0).getDescription());
    }

}
