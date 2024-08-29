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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<OrdererDto> updateShippingStatus(@AuthenticationPrincipal UserAdapter userAdapter,
                                                           @RequestBody ShippingStatusDto statusDto) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        OrdererDto updatedShipping = shippingService.updateShippingStatus(statusDto);
        return ResponseEntity.ok(updatedShipping);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bulk")
    public ResponseEntity<List<OrdererDto>> updateShippingStatusBulk(@AuthenticationPrincipal UserAdapter userAdapter,
                                                                      @RequestBody List<ShippingStatusDto> statusDtos) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<OrdererDto> updatedShippings = shippingService.updateShippingStatusBulk(statusDtos);
        return ResponseEntity.ok(updatedShippings);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrdererDto> getOrderShipping(@NotNull @AuthenticationPrincipal UserAdapter userAdapter,
                                                        @PathVariable String orderId) {
        OrdererDto shippingDto = shippingService.getShippingByOrderId(orderId, userAdapter.getUser());
        return ResponseEntity.ok(shippingDto);
    }
}
