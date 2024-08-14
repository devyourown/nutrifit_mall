package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.ShippingDto;
import kr.co.nutrifit.nutrifit.backend.dto.ShippingStatusDto;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ShippingRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Shipping;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ShippingStatus;
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
    public Shipping createShipping(ShippingDto shippingDto) {
        Order order = orderRepository.findById(shippingDto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 없습니다."));
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
    public Shipping updateShippingStatus(ShippingStatusDto shippingStatusDto) {
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

        return shippingRepository.save(shipping);
    }

    @Transactional
    public List<Shipping> updateShippingStatusBulk(List<ShippingStatusDto> dtos) {
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

        return shippingRepository.saveAll(shippings);
    }
}
