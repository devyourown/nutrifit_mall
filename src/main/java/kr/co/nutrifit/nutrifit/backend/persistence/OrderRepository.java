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

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.shipping s " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.statuses ss " +
            "WHERE ss.status = :status")
    List<Order> findAllByShippingStatus(@Param("status") String status);
}
