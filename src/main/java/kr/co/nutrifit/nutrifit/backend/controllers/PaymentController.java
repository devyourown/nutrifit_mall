package kr.co.nutrifit.nutrifit.backend.controllers;

import jakarta.validation.Valid;
import kr.co.nutrifit.nutrifit.backend.dto.PaymentDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        PaymentDto paymentDto = paymentService.getPaymentById(id);
        return ResponseEntity.ok(paymentDto);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<PaymentDto>> getUserPayments(@AuthenticationPrincipal UserAdapter userAdapter,
                                                            Pageable pageable) {
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<PaymentDto> payments = paymentService.getPaymentsByUser(userAdapter.getUser().getId(), pageable);
        return ResponseEntity.ok(payments);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{userId}")
    public ResponseEntity<Page<PaymentDto>> getUserPaymentsByAdmin(@AuthenticationPrincipal UserAdapter userAdapter,
                                                                   @PathVariable Long userId,
                                                                   Pageable pageable) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<PaymentDto> payments = paymentService.getPaymentsByUser(userId, pageable);
        return ResponseEntity.ok(payments);
    }
}
