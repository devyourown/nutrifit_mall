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
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.product WHERE o.user = :user")
    List<Order> findAllWithItemsAndProductsByUser(@Param("user") User user);

    @Query("SELECT o FROM Order o WHERE o.orderPaymentId = :paymentId")
    Optional<Order> findByOrderPaymentId(@Param("paymentId") String paymentId);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderDto(" +
            "o.orderPaymentId, " +
            "o.orderDate, " +
            "p.paymentStatus, " +
            "ss.status, " +
            "o.totalAmount, " +
            "u.username) " +
            "FROM Order o " +
            "JOIN o.user u " +
            "JOIN o.payment p " +
            "JOIN o.shipping s " +
            "JOIN s.statuses ss " +
            "WHERE ss.statusTime = (SELECT max(ss2.statusTime) FROM ShippingStatus ss2 WHERE ss2.shipping = s) and ss.status = :status")
    Page<OrderDto> findAllByShippingStatusAndPage(@Param("status") String status, Pageable pageable);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderDto(" +
            "o.orderPaymentId, " +
            "o.orderDate, " +
            "p.paymentStatus, " +
            "ss.status, " +
            "o.totalAmount, " +
            "u.username) " +
            "FROM Order o " +
            "JOIN o.user u " +
            "JOIN o.payment p " +
            "JOIN o.shipping s " +
            "JOIN s.statuses ss " +
            "WHERE ss.statusTime = (SELECT max(ss2.statusTime) FROM ShippingStatus ss2 WHERE ss2.shipping = s)")
    Page<OrderDto> findAllOrders(Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.shipping s " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH s.statuses ss " +
            "WHERE ss.status = :status")
    List<Order> findAllByShippingStatus(@Param("status") String status);
}
