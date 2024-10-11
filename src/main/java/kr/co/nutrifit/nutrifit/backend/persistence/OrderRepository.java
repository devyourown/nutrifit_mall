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
            "oi.orderPaymentId, " +
            "oi.productId, " +
            "oi.orderDate, " +
            "oi.currentStatus, " +
            "oi.username, " +
            "oi.trackingNumber, " +
            "oi.totalAmount, " +
            "oi.imageUrl, " +
            "oi.quantity, " +
            "oi.productName) " +
            "FROM OrderItem oi " +
            "WHERE oi.userId = :userId ")
    Page<OrderDto> findAllWithItemsAndProductsByUser(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderDto(" +
            "oi.orderPaymentId, " +
            "oi.productId, " +
            "oi.orderDate, " +
            "oi.currentStatus, " +
            "oi.trackingNumber, " +
            "oi.totalAmount, " +
            "oi.imageUrl, " +
            "oi.quantity, " +
            "oi.productName) " +
            "FROM OrderItem oi " +
            "WHERE oi.orderPaymentId = :paymentId ")
    List<OrderDto> findByOrderPaymentId(@Param("paymentId") String paymentId);
}
