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
            "oi.quantity, " +
            "oi.trackingNumber) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN o.shipping s " +
            "JOIN oi.product p " +
            "JOIN oi.statuses ss " +
            "WHERE ss.statusTime = (SELECT max(ss2.statusTime) FROM ShippingStatus ss2 WHERE ss2.orderItem = oi)")
    List<OrderItemExcelDto> findOrderItemsByStatus(@Param("status") String status);

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
            "WHERE ss.statusTime = (SELECT max(ss2.statusTime) FROM ShippingStatus ss2 WHERE ss2.orderItem = oi)")
    Page<OrderDto> findAllOrders(Pageable pageable);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.OrderDto(" +
            "o.orderPaymentId, " +
            "o.orderDate, " +
            "ss.status, " +
            "u.username," +
            "oi.trackingNumber, " +
            "p.name) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN o.user u " +
            "JOIN oi.statuses ss " +
            "JOIN oi.product p " +
            "WHERE ss.statusTime = (SELECT max(ss2.statusTime) FROM ShippingStatus ss2 WHERE ss2.orderItem = oi) and ss.status = :status")
    Page<OrderDto> findAllByShippingStatusAndPage(@Param("status") String status, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi " +
            "JOIN FETCH oi.order o " +
            "JOIN FETCH oi.product p " +
            "WHERE o.orderPaymentId IN :orderIds " +
            "AND p.name IN :productNames")
    List<OrderItem> findAllByOrderIdInAndProductNameIn(@Param("orderIds") List<String> orderIds,
                                                       @Param("productNames") List<String> productNames);

}
