package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.CouponDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.CouponDto(c.code, c.description, c.discountValue, c.discountType, c.validFrom, c.validUntil, c.minimumOrderAmount, c.maxDiscountAmount, c.remainingQuantity) " +
            "FROM Coupon c")
    Page<CouponDto> findAllCouponsAsDto(Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Coupon c WHERE c.code = :couponCode")
    int deleteByCouponCode(@Param("couponCode") String couponCode);
}
