package kr.co.nutrifit.nutrifit.backend.controllers;

import jakarta.validation.constraints.NotNull;
import kr.co.nutrifit.nutrifit.backend.dto.OrdererDto;
import kr.co.nutrifit.nutrifit.backend.dto.ShippingStatusDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingController {
    private final ShippingService shippingService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrdererDto> getOrderShipping(@NotNull @AuthenticationPrincipal UserAdapter userAdapter,
                                                        @PathVariable String orderId) {
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        OrdererDto shippingDto = shippingService.getShippingByOrderId(orderId, userAdapter.getUser());
        return ResponseEntity.ok(shippingDto);
    }
}
