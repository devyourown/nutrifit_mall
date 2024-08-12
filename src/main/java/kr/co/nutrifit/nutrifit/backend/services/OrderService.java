package kr.co.nutrifit.nutrifit.backend.services;

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
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(String username, List<OrderItemDto> orderItemsDto) {
        User user = userRepository.findByUsername(username);
        Order order = new Order();
        order.setUser(user);

        long totalAmount = 0;

        for (OrderItemDto itemDto : orderItemsDto) {
            Product product = productRepository.findById(itemDto.getProductId()).orElseThrow();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setTotalAmount(product.getPrice() * itemDto.getQuantity());

            totalAmount += orderItem.getTotalAmount();

            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(String username) {
        User user = userRepository.findByUsername(username);
        return orderRepository.findByUser(user);
    }
}
