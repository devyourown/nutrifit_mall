package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.OrderDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderDto(" +
            "o.orderPaymentId, " +
            "p.id, " +
            "o.orderDate, " +
            "ss.status, " +
            "u.username, " +
            "oi.trackingNumber, " +
            "oi.totalAmount, " +
            "oi.imageUrl, " +
            "oi.quantity, " +
            "p.name) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN o.user u " +
            "JOIN oi.statuses ss " +
            "JOIN oi.product p " +
            "WHERE u.id = :userId " +
            "AND ss.statusTime = (SELECT max(ss2.statusTime) FROM ShippingStatus ss2 WHERE ss2.orderItem = oi)")
    Page<OrderDto> findAllWithItemsAndProductsByUser(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderDto(" +
            "o.orderPaymentId, " +
            "p.id, " +
            "o.orderDate, " +
            "ss.status, " +
            "oi.trackingNumber, " +
            "oi.totalAmount, " +
            "oi.imageUrl, " +
            "oi.quantity, " +
            "p.name) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.statuses ss " +
            "JOIN oi.product p " +
            "WHERE o.orderPaymentId = :paymentId " +
            "AND ss.statusTime = (SELECT max(ss2.statusTime) FROM ShippingStatus ss2 WHERE ss2.orderItem = oi)")
    List<OrderDto> findByOrderPaymentId(@Param("paymentId") String paymentId);
}
