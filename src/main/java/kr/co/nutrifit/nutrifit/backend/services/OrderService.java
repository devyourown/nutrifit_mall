package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.*;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderItemRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private static final int BATCH_SIZE = 1000;

    @Transactional
    public Order createOrder(User user, String orderId, List<CartItemDto> cartItemDto) {
        Order order = new Order();
        user.addOrder(order);
        order.setUser(user);
        order.setOrderPaymentId(orderId);
        LocalDateTime now = LocalDateTime.now();

        long totalAmount = 0;

        for (CartItemDto itemDto : cartItemDto) {
            Product product = productRepository.findById(itemDto.getId()).orElseThrow();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setImageUrl(product.getImageUrls().get(0));
            orderItem.setProduct(product);
            orderItem.setPrice(product.getDiscountedPrice());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setTotalAmount(product.getDiscountedPrice() * itemDto.getQuantity());
            orderItem.addStatus(ShippingStatus.builder()
                    .orderItem(orderItem)
                    .statusTime(now)
                    .status("주문완료")
                    .build());

            totalAmount += orderItem.getTotalAmount();

            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrderWithoutUser(String phone, String orderId, List<CartItemDto> cartItemDto) {
        Order order = new Order();
        order.setUserPhone(phone);
        order.setOrderPaymentId(orderId);
        LocalDateTime now = LocalDateTime.now();

        long totalAmount = 0;

        for (CartItemDto itemDto : cartItemDto) {
            Product product = productRepository.findById(itemDto.getId()).orElseThrow();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setImageUrl(product.getImageUrls().get(0));
            orderItem.setProduct(product);
            orderItem.setPrice(product.getDiscountedPrice());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setTotalAmount(product.getDiscountedPrice() * itemDto.getQuantity());
            orderItem.addStatus(ShippingStatus.builder()
                    .orderItem(orderItem)
                    .statusTime(now)
                    .status("주문완료")
                    .build());

            totalAmount += orderItem.getTotalAmount();

            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    public Page<OrderDto> getOrdersByUser(User user, Pageable pageable) {
        return orderRepository.findAllWithItemsAndProductsByUser(user, pageable);
    }

    public Page<OrderDto> getOrders(Pageable pageable) {
        return orderItemRepository.findAllOrders(pageable);
    }

    public Page<OrderDto> getOrdersByFilter(String status, Pageable pageable) {
        return orderItemRepository.findAllByShippingStatusAndPage(status, pageable);
    }

    public List<OrderItemExcelDto> getOrdersForExcelByFilter(String status) {
        return orderItemRepository.findOrderItemsByStatus(status);
    }

    @Transactional
    public void updateTrackingNumbers(List<OrderItemExcelDto> orderItems) {
        List<OrderItem> itemsToSave = new ArrayList<>();
        List<String> orderIds = orderItems.stream().map(OrderItemExcelDto::getOrderId).toList();
        List<String> productNames = orderItems.stream().map(OrderItemExcelDto::getProductName).toList();
        LocalDateTime now = LocalDateTime.now();

        List<OrderItem> foundItems = orderItemRepository.findAllByOrderIdInAndProductNameIn(orderIds, productNames);
        Map<String, OrderItem> orderItemMap = foundItems.stream()
                .collect(Collectors.toMap(
                        item -> item.getOrder().getOrderPaymentId() + "_" + item.getProduct().getName(),
                        item -> item
                ));

        for (OrderItemExcelDto dto : orderItems) {
            String key = dto.getOrderId() + "_" + dto.getProductName();
            OrderItem orderItem = orderItemMap.get(key);
            if (orderItem != null) {
                // Step 5: quantity를 기반으로 기존 OrderItem을 나누어 처리
                int remainingQuantity = dto.getQuantity();
                int availableQuantity = orderItem.getQuantity();

                if (availableQuantity == remainingQuantity) {
                    // Case 1: 전체 수량이 동일하면 바로 운송장 번호 설정
                    orderItem.setTrackingNumber(dto.getTrackingNumber());
                } else if (availableQuantity > remainingQuantity) {
                    // Case 2: 수량이 나누어졌을 경우
                    // 기존 OrderItem의 수량을 남은 부분으로 줄이고, 새로운 OrderItem 생성
                    orderItem.setQuantity(availableQuantity - remainingQuantity);

                    // 새로운 OrderItem 생성 및 운송장 번호 설정
                    OrderItem newOrderItem = new OrderItem();
                    newOrderItem.setOrder(orderItem.getOrder());
                    newOrderItem.setProduct(orderItem.getProduct());
                    newOrderItem.setQuantity(remainingQuantity);
                    newOrderItem.setTrackingNumber(dto.getTrackingNumber());

                    newOrderItem.addStatus(ShippingStatus.builder()
                            .orderItem(newOrderItem)
                            .statusTime(now)
                            .status("출고완료").build());
                    itemsToSave.add(newOrderItem);
                }
                orderItem.addStatus(ShippingStatus.builder()
                        .orderItem(orderItem)
                        .statusTime(now)
                        .status("출고완료").build());
                itemsToSave.add(orderItem);
                if (itemsToSave.size() >= BATCH_SIZE) {
                    orderItemRepository.saveAll(itemsToSave);
                    itemsToSave.clear();
                }
            }
        }
        if (!itemsToSave.isEmpty()) {
            orderItemRepository.saveAll(itemsToSave);
        }
    }

    public List<OrderDto> getNonMemberOrder(String id) {
        return orderRepository.findByOrderPaymentId(id);
    }
}
