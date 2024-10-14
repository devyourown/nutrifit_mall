package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.OrderDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemExcelDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderItemExcelDto(" +
            "oi.orderPaymentId, " +
            "oi.ordererName, " +
            "oi.ordererPhone, " +
            "oi.recipientName, " +
            "oi.recipientPhone, " +
            "oi.address, " +
            "oi.addressDetail, " +
            "oi.cautions, " +
            "oi.productName, " +
            "oi.quantity, " +
            "oi.trackingNumber) " +
            "FROM OrderItem oi " +
            "WHERE oi.currentStatus = :status " +
            "ORDER BY oi.orderDate ASC")
    Page<OrderItemExcelDto> findOrderItemsByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderDto(" +
            "oi.orderPaymentId, " +
            "oi.orderDate, " +
            "oi.currentStatus, " +
            "oi.username, " +
            "oi.trackingNumber, " +
            "oi.productName) " +
            "FROM OrderItem oi")
    Page<OrderDto> findAllOrders(Pageable pageable);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderDto(" +
            "oi.orderPaymentId, " +
            "oi.orderDate, " +
            "oi.currentStatus, " +
            "oi.username," +
            "oi.trackingNumber, " +
            "oi.productName) " +
            "FROM OrderItem oi " +
            "WHERE oi.currentStatus = :status")
    Page<OrderDto> findAllByShippingStatusAndPage(@Param("status") String status, Pageable pageable);

}
