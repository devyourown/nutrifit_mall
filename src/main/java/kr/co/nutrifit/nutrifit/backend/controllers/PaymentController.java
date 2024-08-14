package kr.co.nutrifit.nutrifit.backend.controllers;

import jakarta.validation.Valid;
import kr.co.nutrifit.nutrifit.backend.dto.PaymentDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Payment;
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
    public ResponseEntity<Payment> createPayment(
            @AuthenticationPrincipal UserAdapter userAdapter,
            @RequestBody @Valid PaymentDto paymentDto
            ) {
        Payment payment = paymentService.createPayment(userAdapter.getUser(), paymentDto);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserAdapter userAdapter
    ) {
        Payment payment = paymentService.getPaymentByIdAndUser(id, userAdapter.getUser());
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Payment>> getUserPayments(@AuthenticationPrincipal UserAdapter userAdapter) {
        List<Payment> payments = paymentService.getPaymentsByUser(userAdapter.getUser());
        return ResponseEntity.ok(payments);
    }
}
