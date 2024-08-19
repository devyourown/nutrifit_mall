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

    @PostMapping("/create")
    public ResponseEntity<PaymentDto> createPayment(
            @AuthenticationPrincipal UserAdapter userAdapter,
            @RequestBody @Valid PaymentDto paymentDto
            ) {
        PaymentDto result = paymentService.createPayment(userAdapter.getUser(), paymentDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPayment(
            @PathVariable Long id,
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
