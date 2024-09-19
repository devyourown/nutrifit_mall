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
            "o.orderDate, " +
            "ss.status, " +
            "u.username, " +
            "oi.trackingNumber, " +
            "p.name) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN o.user u " +
            "JOIN oi.statuses ss " +
            "JOIN oi.product p " +
            "WHERE o.user = :user " +
            "AND ss.statusTime = (SELECT max(ss2.statusTime) FROM ShippingStatus ss2 WHERE ss2.orderItem = oi)")
    Page<OrderDto> findAllWithItemsAndProductsByUser(@Param("user") User user, Pageable pageable);


    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderDto(" +
            "o.orderPaymentId, " +
            "o.orderDate, " +
            "ss.status, " +
            "oi.trackingNumber, " +
            "o.totalAmount, " +
            "p.name) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.statuses ss " +
            "JOIN oi.product p " +
            "WHERE o.orderPaymentId = :paymentId " +
            "AND ss.statusTime = (SELECT max(ss2.statusTime) FROM ShippingStatus ss2 WHERE ss2.orderItem = oi)")
    List<OrderDto> findByOrderPaymentId(@Param("paymentId") String paymentId);
}
