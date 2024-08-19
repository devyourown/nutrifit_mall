package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.ShippingDto;
import kr.co.nutrifit.nutrifit.backend.dto.ShippingStatusDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Shipping;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.ShippingService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
    public ResponseEntity<ShippingDto> updateShippingStatus(@AuthenticationPrincipal UserAdapter userAdapter,
                                                            @RequestBody ShippingStatusDto statusDto) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ShippingDto updatedShipping = shippingService.updateShippingStatus(statusDto);
        return ResponseEntity.ok(updatedShipping);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bulk")
    public ResponseEntity<List<ShippingDto>> updateShippingStatusBulk(@AuthenticationPrincipal UserAdapter userAdapter,
                                                                      @RequestBody List<ShippingStatusDto> statusDtos) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<ShippingDto> updatedShippings = shippingService.updateShippingStatusBulk(statusDtos);
        return ResponseEntity.ok(updatedShippings);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ShippingDto> getOrderShipping(@NotNull @AuthenticationPrincipal UserAdapter userAdapter,
                                                        @PathVariable Long orderId) {
        ShippingDto shippingDto = shippingService.getShippingByOrderId(orderId, userAdapter.getUser());
        return ResponseEntity.ok(shippingDto);
    }
}
