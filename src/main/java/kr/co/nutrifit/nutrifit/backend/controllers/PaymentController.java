package kr.co.nutrifit.nutrifit.backend.controllers;

import jakarta.validation.Valid;
import kr.co.nutrifit.nutrifit.backend.dto.PaymentDto;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> createPayment(
            @AuthenticationPrincipal UserAdapter userAdapter,
            @RequestBody @Valid PaymentDto paymentDto
            ) {
        try {
            paymentService.createPayment(userAdapter.getUser(), paymentDto);
            return ResponseEntity.ok("결제가 완료되었습니다.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPayment(
            @PathVariable String id,
            @AuthenticationPrincipal UserAdapter userAdapter
    ) {
        PaymentDto paymentDto = paymentService.getPaymentByIdAndUser(id, userAdapter.getUser());
        return ResponseEntity.ok(paymentDto);
    }

    @GetMapping("/user")
    public ResponseEntity<List<PaymentDto>> getUserPayments(@AuthenticationPrincipal UserAdapter userAdapter) {
        List<PaymentDto> payments = paymentService.getPaymentsByUser(userAdapter.getUser());
        return ResponseEntity.ok(payments);
    }
}
