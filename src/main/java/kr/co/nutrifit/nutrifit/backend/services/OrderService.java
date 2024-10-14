package kr.co.nutrifit.nutrifit.backend.services;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderItemRepository orderItemRepository;
    private static final int BATCH_SIZE = 10000;

    @Transactional
    public Order createOrder(Optional<User> user, String phone, String orderId, List<CartItemDto> cartItemDto, OrdererDto ordererDto) {
        Order order = new Order();
        order.setOrderPaymentId(orderId);
        LocalDateTime now = LocalDateTime.now();

        if (user.isPresent()) {
            order.setUser(user.get());
            user.get().addOrder(order);
        } else {
            order.setUserPhone(phone);
        }

        List<Long> productIds = cartItemDto.stream()
                .map(CartItemDto::getId)
                .collect(Collectors.toList());
        Map<Long, Product> productMap = productService.getProductsByIds(productIds);

        long totalAmount = 0;

        for (CartItemDto itemDto : cartItemDto) {
            Product product = productMap.get(itemDto.getId());
            if (product == null) throw new IllegalArgumentException("상품을 찾을 수 없습니다: " + itemDto.getId());

            OrderItem orderItem = createOrderItem(order, user.orElse(null), phone, now, ordererDto, product, itemDto);
            totalAmount += orderItem.getTotalAmount();
            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    private OrderItem createOrderItem(Order order, User user, String phone, LocalDateTime now, OrdererDto ordererDto, Product product, CartItemDto itemDto) {
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

    public Page<OrderDto> getOrders(Pageable pageable) {
        return orderItemRepository.findAllOrders(pageable);
    }

    public Page<OrderDto> getOrdersByFilter(String status, Pageable pageable) {
        return orderItemRepository.findAllByShippingStatusAndPage(status, pageable);
    }

    public List<OrderItemExcelDto> getOrdersForExcelByFilter(String status, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<OrderItemExcelDto> resultPage = orderItemRepository.findOrderItemsByStatus(status, pageable);
        return resultPage.getContent();
    }

    public void updateTrackingNumbers(List<OrderItemExcelDto> orderItems, LocalDateTime startDate, LocalDateTime endDate) {
        for (int i = 0; i < orderItems.size(); i += BATCH_SIZE) {
            List<OrderItemExcelDto> partitionItems = orderItems.subList(i, Math.min(i + BATCH_SIZE, orderItems.size()));
            List<OrderItem> foundItems = findOrderItems(partitionItems, startDate, endDate);
            for (OrderItem item : foundItems) {
                System.out.println(item.getId());
            }
            Map<String, OrderItem> orderItemMap = createOrderItemMap(foundItems);
            processAndUpdateOrderItems(partitionItems, orderItemMap, LocalDateTime.now());
        }
    }

    private List<OrderItem> findOrderItems(List<OrderItemExcelDto> orderItems, LocalDateTime startDate, LocalDateTime endDate) {
        List<String> orderIds = orderItems.stream().map(OrderItemExcelDto::getOrderId).toList();
        List<String> productNames = orderItems.stream().map(OrderItemExcelDto::getProductName).toList();
        return orderItemRepository.findAllByOrderIdInAndProductNameInAndOrderDateBetween(orderIds, productNames, startDate, endDate);
    }

    private Map<String, OrderItem> createOrderItemMap(List<OrderItem> foundItems) {
        return foundItems.stream()
                .collect(Collectors.toMap(
                        item -> item.getOrderPaymentId() + "_" + item.getProductName(),
                        item -> item
                ));
    }

    private void processAndUpdateOrderItems(List<OrderItemExcelDto> orderItems, Map<String, OrderItem> orderItemMap, LocalDateTime now) {
        for (OrderItemExcelDto dto : orderItems) {
            String key = dto.getOrderId() + "_" + dto.getProductName();
            OrderItem orderItem = orderItemMap.get(key);
            if (orderItem != null) {
                orderItem.setTrackingNumber(dto.getTrackingNumber());
                orderItem.setCurrentStatus("출고완료");
                orderItem.setCurrentStatusTime(now);
                orderItem.addStatus(ShippingStatus.builder()
                        .orderItem(orderItem)
                        .statusTime(now)
                        .status("출고완료").build());
            }
        }
        saveOrderItems();
    }

    @Transactional
    private void saveOrderItems() {
        orderItemRepository.flush();
    }
}
