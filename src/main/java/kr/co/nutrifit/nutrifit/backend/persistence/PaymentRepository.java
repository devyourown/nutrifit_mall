package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.PaymentDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Payment;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT new com.example.dto.PaymentDto(" +
            "p.orderPaymentId, p.total, p.subtotal, p.discount, p.shippingFee, p.paymentMethod, " +
            "p.recipientName, p.recipientPhone, p.ordererName, p.ordererPhone, " +
            "p.address, p.addressDetail, p.cautions, p.paymentDate, p.couponCode, " +
            "p.usedPoints, p.earnPoints, p.phoneNumber) " +
            "FROM Payment p " +
            "WHERE p.orderPaymentId = :paymentId")
    Optional<PaymentDto> findByOrderPaymentId(@Param("paymentId") String paymentId);

    // 2. 특정 userId로 해당 사용자의 모든 Payment 리스트 조회
    @Query("SELECT new com.example.dto.PaymentDto(" +
            "p.orderPaymentId, p.total, p.subtotal, p.discount, p.shippingFee, p.paymentMethod, " +
            "p.recipientName, p.recipientPhone, p.ordererName, p.ordererPhone, " +
            "p.address, p.addressDetail, p.cautions, p.paymentDate, p.couponCode, " +
            "p.usedPoints, p.earnPoints, p.phoneNumber) " +
            "FROM Payment p " +
            "WHERE p.user.id = :userId")
    Page<PaymentDto> findPaymentDtosByUserId(@Param("userId") Long userId, Pageable pageable);

}
