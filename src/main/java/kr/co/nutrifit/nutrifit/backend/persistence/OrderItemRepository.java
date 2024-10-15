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
            "FROM OrderItem oi " +
            "WHERE oi.orderDate BETWEEN :startDate AND :endDate " +
            "ORDER BY oi.orderDate DESC")
    Page<OrderDto> findAllOrders(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 Pageable pageable);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderDto(" +
            "oi.orderPaymentId, " +
            "oi.orderDate, " +
            "oi.currentStatus, " +
            "oi.username," +
            "oi.trackingNumber, " +
            "oi.productName) " +
            "FROM OrderItem oi " +
            "WHERE oi.currentStatus = :status " +
            "AND oi.orderDate BETWEEN :startDate AND :endDate " +
            "ORDER BY oi.orderDate DESC")
    Page<OrderDto> findAllByFilterBetweenDate(@Param("status") String status,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate,
                                                  Pageable pageable);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto(" +
            "oi.productId, " +
            "oi.productName, " +
            "oi.imageUrl, " +
            "oi.price, " +
            "oi.quantity, " +
            "oi.totalAmount) " +
            "FROM OrderItem oi " +
            "WHERE oi.orderPaymentId = :paymentId " +
            "ORDER BY oi.orderDate DESC")
    List<OrderItemDto> findItemsByPaymentId(@Param("paymentId") String paymentId);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderDto(" +
            "oi.orderPaymentId, " +
            "oi.orderDate, " +
            "oi.currentStatus, " +
            "oi.username, " +
            "oi.trackingNumber, " +
            "oi.productName) " +
            "FROM OrderItem oi " +
            "WHERE (oi.orderPaymentId LIKE :query% " +
            "OR oi.productName LIKE :query% " +
            "OR oi.username LIKE :query% " +
            "OR oi.trackingNumber LIKE :query%) " +
            "AND oi.currentStatus = :status " +
            "AND oi.orderDate BETWEEN :startDate AND :endDate")
    Page<OrderDto> findAllByQueryBetweenDate(@Param("status") String status,
                                            @Param("query") String query,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             Pageable pageable);
}
