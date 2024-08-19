package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.ShippingDto;
import kr.co.nutrifit.nutrifit.backend.dto.ShippingStatusDto;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ShippingRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Shipping;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ShippingStatus;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShippingService {
    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Shipping createShipping(ShippingDto shippingDto, Order order) {
        Shipping shipping = Shipping
                .builder()
                .order(order)
                .recipientName(shippingDto.getRecipientName())
                .address(shippingDto.getAddress())
                .phoneNumber(shippingDto.getPhoneNumber())
                .shippingStatus(ShippingStatus.ORDERED)
                .orderDate(LocalDateTime.now())
                .build();
        return shippingRepository.save(shipping);
    }

    @Transactional
    public ShippingDto updateShippingStatus(ShippingStatusDto shippingStatusDto) {
        Shipping shipping = shippingRepository.findById(shippingStatusDto.getShippingId())
                .orElseThrow(() -> new IllegalArgumentException("배송 정보가 없습니다."));
        ShippingStatus status = shippingStatusDto.getStatus();
        shipping.setShippingStatus(status);

        if (status == ShippingStatus.CANCELLED) {
            shipping.setCancelledDate(LocalDateTime.now());
        } else if (status == ShippingStatus.SHIPPED) {
            shipping.setShippedDate(LocalDateTime.now());
        } else if (status == ShippingStatus.PENDING) {
            shipping.setPendingDate(LocalDateTime.now());
        } else if (status == ShippingStatus.DELIVERED) {
            shipping.setDeliveredDate(LocalDateTime.now());
        } else if (status == ShippingStatus.REFUNDED) {
            shipping.setRefundDate(LocalDateTime.now());
        }

        return convertToDto(shippingRepository.save(shipping));
    }

    @Transactional
    public List<ShippingDto> updateShippingStatusBulk(List<ShippingStatusDto> dtos) {
        List<Long> ids = dtos.stream()
                .map(ShippingStatusDto::getShippingId)
                .collect(Collectors.toList());

        List<Shipping> shippings = shippingRepository.findAllById(ids);

        shippings.forEach(shipping -> {
            ShippingStatusDto dto = dtos.stream()
                    .filter(d -> d.getShippingId().equals(shipping.getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("배송 정보가 없습니다."));

            ShippingStatus status = dto.getStatus();
            shipping.setShippingStatus(status);

            if (status == ShippingStatus.CANCELLED) {
                shipping.setCancelledDate(LocalDateTime.now());
            } else if (status == ShippingStatus.SHIPPED) {
                shipping.setShippedDate(LocalDateTime.now());
            } else if (status == ShippingStatus.PENDING) {
                shipping.setPendingDate(LocalDateTime.now());
            } else if (status == ShippingStatus.DELIVERED) {
                shipping.setDeliveredDate(LocalDateTime.now());
            } else if (status == ShippingStatus.REFUNDED) {
                shipping.setRefundDate(LocalDateTime.now());
            }
        });

        return shippingRepository.saveAll(shippings)
                .stream().map(this::convertToDto)
                .toList();
    }

    public ShippingDto getShippingByOrderId(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
        if (!order.getUser().equals(user)) {
            throw new SecurityException("해당 주문에 접근할 권한이 없습니다.");
        }
        Shipping shipping =  shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문에 대한 배송 정보를 찾을 수 없습니다."));
        return convertToDto(shipping);
    }

    private ShippingDto convertToDto(Shipping shipping) {
        return ShippingDto.builder()
                .id(shipping.getId())
                .recipientName(shipping.getRecipientName())
                .address(shipping.getAddress())
                .currentStatus(shipping.getShippingStatus())
                .order(shipping.getOrder())
                .phoneNumber(shipping.getPhoneNumber())
                .build();
    }
}
