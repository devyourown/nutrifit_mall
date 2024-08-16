package kr.co.nutrifit.nutrifit.backend.services;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import kr.co.nutrifit.nutrifit.backend.dto.PaymentDto;
import kr.co.nutrifit.nutrifit.backend.persistence.OrderRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.PaymentRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Payment;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final IamportClient iamportClient;

    @Transactional
    public Payment createPayment(User user, PaymentDto paymentDto) {
        try {
            IamportResponse<com.siot.IamportRestClient.response.Payment> response = iamportClient.paymentByImpUid(paymentDto.getImpUid());
            if (response.getResponse() == null) {
                throw new IllegalArgumentException("결제 정보가 유효하지 않습니다.");
            }
            com.siot.IamportRestClient.response.Payment iamportPayment = response.getResponse();


            validatePaymentAmount(iamportPayment, paymentDto);
            Order order = orderRepository.findById(paymentDto.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
            Payment payment = createPaymentEntity(user, paymentDto, iamportPayment, order);

            order.setPayment(payment);
            return paymentRepository.save(payment);
        } catch (IamportResponseException e) {
            throw new RuntimeException("아임포트 API 요청에 실패했습니다: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("결제 처리 중 네트워크 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private void validatePaymentAmount(com.siot.IamportRestClient.response.Payment iamportPayment, PaymentDto paymentDto) {
        if (iamportPayment.getAmount().compareTo(BigDecimal.valueOf(paymentDto.getAmount())) != 0) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }
    }

    private Payment createPaymentEntity(User user, PaymentDto paymentDto, com.siot.IamportRestClient.response.Payment iamportPayment, Order order) {
        return Payment.builder()
                .impUid(iamportPayment.getImpUid())
                .amount(paymentDto.getAmount())
                .merchantUid(iamportPayment.getMerchantUid())
                .paymentMethod(iamportPayment.getPayMethod())
                .paymentStatus(iamportPayment.getStatus())
                .paymentDate(LocalDateTime.now())
                .user(user)
                .order(order)
                .build();
    }

    public Payment getPaymentByIdAndUser(Long id, User user) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 없습니다."));
        if (!payment.getUser().getId().equals(user.getId())) {
            throw new SecurityException("허용되지 않은 접근입니다.");
        }
        return payment;
    }

    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByUser(user);
    }
}
