package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.ShippingDto;
import kr.co.nutrifit.nutrifit.backend.dto.ShippingStatusDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Shipping;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingController {
    private final ShippingService shippingService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<Shipping> updateShippingStatus(@RequestBody ShippingStatusDto statusDto) {
        Shipping updatedShipping = shippingService.updateShippingStatus(statusDto);
        return ResponseEntity.ok(updatedShipping);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bulk")
    public ResponseEntity<List<Shipping>> updateShippingStatusBulk(@RequestBody List<ShippingStatusDto> statusDtos) {
        List<Shipping> updatedShippings = shippingService.updateShippingStatusBulk(statusDtos);
        return ResponseEntity.ok(updatedShippings);
    }

    @GetMapping
    public ResponseEntity<Shipping> getOrderShipping(@AuthenticationPrincipal UserAdapter userAdapter,
                                                     @PathVariable Long orderId) {
        Shipping shipping = shippingService.getShippingByOrderId(orderId, userAdapter.getUser());
        return ResponseEntity.ok(shipping);
    }
}
