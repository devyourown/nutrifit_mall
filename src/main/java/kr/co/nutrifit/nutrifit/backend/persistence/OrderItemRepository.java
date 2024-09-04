package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto(" +
            "oi.product.id, oi.quantity, oi.totalAmount, oi.product.name) " +
            "FROM OrderItem oi " +
            "WHERE oi.order.orderPaymentId = :orderPaymentId")
    List<OrderItemDto> findAllByOrderPaymentId(@Param("orderPaymentId") String orderPaymentId);
}
