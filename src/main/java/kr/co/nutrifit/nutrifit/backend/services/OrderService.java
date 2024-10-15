package kr.co.nutrifit.nutrifit.backend.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import kr.co.nutrifit.nutrifit.backend.dto.*;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderItemRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderItemRepository orderItemRepository;
    private final DataSource dataSource;
    private final EntityManager entityManager;
    private static final int BATCH_SIZE = 1000;

    @Transactional
    public Order createOrder(Optional<User> user, String phone, String orderId, List<OrderItemDto> orderItemDto, OrdererDto ordererDto) {
        Order order = new Order();
        order.setOrderPaymentId(orderId);
        LocalDateTime now = LocalDateTime.now();

        if (user.isPresent()) {
            order.setUser(user.get());
            user.get().addOrder(order);
        } else {
            order.setUserPhone(phone);
        }

        List<Long> productIds = orderItemDto.stream()
                .map(OrderItemDto::getProductId)
                .collect(Collectors.toList());
        Map<Long, Product> productMap = productService.getProductsByIds(productIds);

        long totalAmount = 0;

        for (OrderItemDto itemDto : orderItemDto) {
            Product product = productMap.get(itemDto.getProductId());
            if (product == null) throw new IllegalArgumentException("상품을 찾을 수 없습니다: " + itemDto.getProductId());

            OrderItem orderItem = createOrderItem(order, user.orElse(null), phone, now, ordererDto, product, itemDto);
            totalAmount += orderItem.getTotalAmount();
            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    private OrderItem createOrderItem(Order order, User user, String phone, LocalDateTime now, OrdererDto ordererDto, Product product, OrderItemDto itemDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        if (user != null) {
            orderItem.setUserId(user.getId());
            orderItem.setUsername(user.getUsername());
        } else {
            orderItem.setUsername(phone);
        }
        orderItem.setOrderPaymentId(order.getOrderPaymentId());
        orderItem.setCurrentStatus("주문완료");
        orderItem.setCurrentStatusTime(now);
        orderItem.setOrderDate(now);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setPrice(product.getDiscountedPrice());
        orderItem.setQuantity(itemDto.getQuantity());
        orderItem.setTotalAmount(product.getDiscountedPrice() * itemDto.getQuantity());
        orderItem.setImageUrl(product.getImageUrls().get(0));
        orderItem.setOrdererName(ordererDto.getOrdererName());
        orderItem.setOrdererPhone(ordererDto.getOrdererPhone());
        orderItem.setRecipientName(ordererDto.getRecipientName());
        orderItem.setRecipientPhone(ordererDto.getRecipientPhone());
        orderItem.setAddress(ordererDto.getAddress());
        orderItem.setAddressDetail(ordererDto.getAddressDetail());
        orderItem.setCautions(ordererDto.getCautions());
        orderItem.addStatus(ShippingStatus.builder()
                .orderItem(orderItem)
                .statusTime(now)
                .status("주문완료").build());
        return orderItem;
    }

    public Page<OrderDto> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findAllWithItemsAndProductsByUser(userId, pageable);
    }

    public List<OrderDto> getNonMemberOrder(String id) {
        return orderRepository.findByOrderPaymentId(id);
    }

    public Page<OrderDto> getOrders(Pageable pageable, LocalDateTime startDate, LocalDateTime endDate) {
        return orderItemRepository.findAllOrders(startDate, endDate, pageable);
    }

    public Page<OrderDto> getOrdersByFilterBetweenDate(String status, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate) {
        return orderItemRepository.findAllByFilterBetweenDate(status, startDate, endDate, pageable);
    }

    public Page<OrderDto> getOrdersByQuery(String query, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate) {
        return orderItemRepository.findAllByQueryBetweenDate(query, startDate, endDate, pageable);
    }

    public List<OrderItemDto> getItemsByPaymentId(String paymentId) {
        return orderItemRepository.findItemsByPaymentId(paymentId);
    }

    public List<OrderItemExcelDto> getOrdersForExcelByFilter(String status, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<OrderItemExcelDto> resultPage = orderItemRepository.findOrderItemsByStatus(status, pageable);
        return resultPage.getContent();
    }

    @Transactional
    public void updateTrackingNumbers(List<OrderItemExcelDto> orderItems) {
        String sql = """
            UPDATE order_item
            SET tracking_number = ?, current_status = ?, current_status_time = ?
            WHERE order_payment_id = ? AND product_name = ?
        """;

        LocalDateTime now = LocalDateTime.now();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            int count = 0;
            for (OrderItemExcelDto dto : orderItems) {
                ps.setString(1, dto.getTrackingNumber());
                ps.setString(2, "출고완료");
                ps.setTimestamp(3, Timestamp.valueOf(now));
                ps.setString(4, dto.getOrderId());
                ps.setString(5, dto.getProductName());
                ps.addBatch();

                // 배치 사이즈마다 실행
                if (++count % BATCH_SIZE == 0) {
                    ps.executeBatch();
                }
            }

            // 남아 있는 배치 실행
            ps.executeBatch();
            entityManager.flush();
            entityManager.clear();
        } catch (Exception e) {
            throw new RuntimeException("Batch update failed", e);
        }
    }
}
