package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.CouponDto;
import kr.co.nutrifit.nutrifit.backend.persistence.CouponRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserCouponRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Coupon;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;

    public void createCoupon(CouponDto couponDto) {
        Coupon coupon = Coupon.builder()
                .code(couponDto.getCode())
                .description(couponDto.getDescription())
                .discountType(couponDto.getDiscountType())
                .discountValue(couponDto.getDiscountValue())
                .validFrom(couponDto.getValidFrom())
                .validUntil(couponDto.getValidUntil())
                .minimumOrderAmount(couponDto.getMinimumOrderAmount())
                .maxDiscountAmount(couponDto.getMaxDiscountAmount())
                .isActive(true)
                .remainingQuantity(couponDto.getRemainingQuantity())
                .build();
        couponRepository.save(coupon);
    }

    public Optional<Coupon> findCouponByCode(String code) {
        return couponRepository.findByCode(code)
                .filter(coupon -> coupon.isActive()
                        && coupon.getRemainingQuantity() > 0
                        && LocalDateTime.now().isAfter(coupon.getValidFrom())
                        && LocalDateTime.now().isBefore(coupon.getValidUntil()));
    }

    @Transactional
    public void assignCouponToUser(String code, User user) {
        Coupon coupon = findCouponByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않거나 만료된 쿠폰입니다."));

        if (coupon.getRemainingQuantity() <= 0) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }

        boolean alreadyAssigned = userCouponRepository.existsByUserAndCoupon(user, coupon);
        if (alreadyAssigned) {
            throw new IllegalStateException("해당 쿠폰은 이미 할당 되었습니다.");
        }
        coupon.setRemainingQuantity(coupon.getRemainingQuantity() - 1);
        couponRepository.save(coupon);

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setAssignedAt(LocalDateTime.now());
        userCoupon.setUsed(false);

        userCouponRepository.save(userCoupon);
    }

    @Transactional
    public void useCoupon(User user, String couponCode, Long orderAmount) {
        UserCoupon userCoupon = userCouponRepository.findByCodeAndUser(couponCode, user)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 없거나 잘못된 쿠폰입니다."));

        if (userCoupon.isUsed()) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        Coupon coupon = userCoupon.getCoupon();

        // 유효 기간 검증
        if (LocalDateTime.now().isAfter(coupon.getValidUntil())) {
            throw new IllegalStateException("쿠폰이 만료 되었습니다.");
        }

        if (orderAmount < coupon.getMinimumOrderAmount()) {
            throw new IllegalStateException("최소 주문 금액을 충족하지 못했습니다.");
        }

        userCoupon.setUsed(true);
        userCoupon.setUsedAt(LocalDateTime.now());

        userCouponRepository.save(userCoupon);
    }

    @Transactional
    public String deleteCoupon(String couponCode) {
        try {
            userCouponRepository.deleteByCouponCode(couponCode);
            int result = couponRepository.deleteByCouponCode(couponCode);
            if (result == 1) {
                return "쿠폰이 정상적으로 삭제 되었습니다.";
            }
            return "쿠폰이 " + result + "개 삭제 되었습니다.";
        } catch (Exception e) {
            return "쿠폰 삭제에 실패했습니다.";
        }
    }

    public Page<CouponDto> getUserCoupon(Long userId, Pageable pageable) {
        return userCouponRepository.findAllByUserWithDto(userId, pageable);
    }

    public Page<CouponDto> getAllCoupons(Pageable pageable) {
        return couponRepository.findAllCouponsAsDto(pageable);
    }
}
