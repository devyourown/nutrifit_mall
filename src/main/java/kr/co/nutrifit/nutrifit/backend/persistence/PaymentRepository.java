package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.persistence.entities.Payment;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 1. 특정 userId와 paymentId로 Payment 정보 조회
    @Query("SELECT DISTINCT p FROM Payment p " +
            "JOIN FETCH p.order o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH o.shipping s " +
            "JOIN FETCH oi.product product " +
            "WHERE p.id = :paymentId AND p.user.id = :userId")
    Optional<Payment> findByIdWithOrderAndItemsAndShipping(@Param("userId") Long userId, @Param("paymentId") Long paymentId);

    // 2. 특정 userId로 해당 사용자의 모든 Payment 리스트 조회
    @Query("SELECT DISTINCT p FROM Payment p " +
            "JOIN FETCH p.order o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH o.shipping s " +
            "JOIN FETCH oi.product product " +
            "WHERE p.user.id = :userId")
    List<Payment> findByUserWithOrdersAndItemsAndShipping(@Param("userId") Long userId);}
