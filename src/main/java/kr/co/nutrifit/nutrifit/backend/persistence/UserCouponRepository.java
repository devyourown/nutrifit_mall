package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.persistence.entities.Coupon;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    Optional<UserCoupon> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserAndCoupon(User user, Coupon coupon);
}
