package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemExcelDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderItemExcelDto(" +
            "o.orderPaymentId, " +
            "s.ordererName, " +
            "s.ordererPhone, " +
            "s.recipientName, " +
            "s.recipientPhone, " +
            "s.address, " +
            "s.addressDetail, " +
            "s.cautions, " +
            "p.name, " +
            "oi.quantity) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN o.shipping s " +
            "JOIN oi.product p " +
            "JOIN s.statuses ss " +
            "WHERE ss.status = :status")
    List<OrderItemExcelDto> findOrderItemsByStatus(@Param("status") String status);

}
