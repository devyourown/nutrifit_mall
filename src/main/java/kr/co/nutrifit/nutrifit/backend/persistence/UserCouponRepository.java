package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.CouponDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Coupon;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    @Query("SELECT uc FROM UserCoupon uc JOIN uc.coupon c WHERE uc.user = :user AND c.code = :code")
    Optional<UserCoupon> findByCodeAndUser(@Param("code") String code, @Param("user") User user);
    boolean existsByUserAndCoupon(User user, Coupon coupon);
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.CouponDto(" +
            "uc.coupon.code, " +
            "uc.coupon.description, " +
            "uc.coupon.discountValue, " +
            "uc.coupon.discountType, " +
            "uc.coupon.validFrom, " +
            "uc.coupon.validUntil, " +
            "uc.coupon.minimumOrderAmount, " +
            "uc.coupon.maxDiscountAmount, " +
            "uc.coupon.remainingQuantity) " +
            "FROM UserCoupon uc " +
            "WHERE uc.user.id = :userId")
    Page<CouponDto> findAllByUserWithDto(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM UserCoupon uc WHERE uc.coupon.code = :couponCode")
    void deleteByCouponCode(@Param("couponCode") String couponCode);
}
