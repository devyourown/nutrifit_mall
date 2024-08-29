package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.OrdererDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShippingService {
    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Shipping createShipping(OrdererDto ordererDto, Order order) {
        Shipping shipping = Shipping
                .builder()
                .order(order)
                .recipientName(ordererDto.getRecipientName())
                .recipientPhone(ordererDto.getRecipientPhone())
                .ordererName(ordererDto.getOrdererName())
                .ordererPhone(ordererDto.getOrdererPhone())
                .address(ordererDto.getAddress())
                .addressDetail(ordererDto.getAddressDetail())
                .cautions(ordererDto.getCautions())
                .build();
        ShippingStatus status = ShippingStatus.builder()
                .shipping(shipping)
                .time(LocalDateTime.now())
                .status("결제 완료")
                .build();
        shipping.addStatus(status);
        return shippingRepository.save(shipping);
    }

    @Transactional
    public OrdererDto updateShippingStatus(ShippingStatusDto shippingStatusDto) {
        Shipping shipping = shippingRepository.findById(shippingStatusDto.getShippingId())
                .orElseThrow(() -> new IllegalArgumentException("배송 정보가 없습니다."));
        ShippingStatus status = shippingStatusDto.getStatus();
        shipping.addStatus(status);

        return convertToDto(shippingRepository.save(shipping));
    }

    @Transactional
    public List<OrdererDto> updateShippingStatusBulk(List<ShippingStatusDto> dtos) {
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
            shipping.addStatus(status);
        });

        return shippingRepository.saveAll(shippings)
                .stream().map(this::convertToDto)
                .toList();
    }

    public OrdererDto getShippingByOrderId(String orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
        if (!order.getUser().equals(user)) {
            throw new SecurityException("해당 주문에 접근할 권한이 없습니다.");
        }
        Shipping shipping =  shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문에 대한 배송 정보를 찾을 수 없습니다."));
        return convertToDto(shipping);
    }

    private OrdererDto convertToDto(Shipping shipping) {
        return OrdererDto.builder()
                .recipientName(shipping.getRecipientName())
                .recipientPhone(shipping.getRecipientPhone())
                .address(shipping.getAddress())
                .addressDetail(shipping.getAddressDetail())
                .cautions(shipping.getCautions())
                .build();
    }
}
