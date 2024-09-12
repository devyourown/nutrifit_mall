package kr.co.nutrifit.nutrifit.backend.controllers;

import jakarta.validation.Valid;
import kr.co.nutrifit.nutrifit.backend.dto.PaymentDto;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            paymentService.createPayment(userAdapter.getUser(), paymentDto);
            return ResponseEntity.ok("결제가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/nonmember")
    public ResponseEntity<?> createPaymentWithoutUser(
            @RequestBody @Valid PaymentDto paymentDto
    ) {
        try {
            paymentService.createPaymentWithoutUser(paymentDto);
            return ResponseEntity.ok("결제가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPayment(
            @PathVariable String id
    ) {
        PaymentDto paymentDto = paymentService.getPaymentByIdAndUser(id);
        return ResponseEntity.ok(paymentDto);
    }

    @GetMapping("/user")
    public ResponseEntity<List<PaymentDto>> getUserPayments(@AuthenticationPrincipal UserAdapter userAdapter) {
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<PaymentDto> payments = paymentService.getPaymentsByUser(userAdapter.getUser());
        return ResponseEntity.ok(payments);
    }
}
