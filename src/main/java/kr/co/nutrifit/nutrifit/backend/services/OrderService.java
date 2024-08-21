package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.OrderDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderItemRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.OrderItem;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(User user, List<OrderItemDto> orderItemsDto) {
        Order order = new Order();
        order.setUser(user);

        long totalAmount = 0;

        for (OrderItemDto itemDto : orderItemsDto) {
            Product product = productRepository.findById(itemDto.getProductId()).orElseThrow();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setPrice(product.getDiscountedPrice());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setTotalAmount(product.getDiscountedPrice() * itemDto.getQuantity());

            totalAmount += orderItem.getTotalAmount();

            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    public List<OrderDto> getOrdersByUser(User user) {
        return orderRepository.findAllWithItemsAndProductsByUser(user)
                .stream().map(this::convertToDto)
                .toList();
    }

    private OrderDto convertToDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .orderItems(order.getOrderItems().stream().map(this::convertToItemDto).toList())
                .build();
    }

    private OrderItemDto convertToItemDto(OrderItem orderItem) {
        Product product = orderItem.getProduct();
        return OrderItemDto.builder()
                .productId(orderItem.getId())
                .quantity(orderItem.getQuantity())
                .totalAmount(orderItem.getTotalAmount())
                .imageUrl(product.getImageUrls().get(0))
                .name(product.getName())
                .build();
    }
}
