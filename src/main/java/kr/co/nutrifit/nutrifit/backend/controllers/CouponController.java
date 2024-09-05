package kr.co.nutrifit.nutrifit.backend.controllers;

import jakarta.validation.Valid;
import kr.co.nutrifit.nutrifit.backend.dto.CouponDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Coupon;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Void> createCoupon(@AuthenticationPrincipal UserAdapter userAdapter,
                                             @RequestBody @Valid CouponDto couponDto) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        couponService.createCoupon(couponDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignCouponToUser(@RequestParam String code,
                                                     @AuthenticationPrincipal UserAdapter userAdapter) {
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        couponService.assignCouponToUser(code, userAdapter.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CouponDto>> getUserCoupon(@AuthenticationPrincipal UserAdapter userAdapter) {
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<CouponDto> coupons = couponService.getUserCoupon(userAdapter.getUser());
        return ResponseEntity.ok(coupons);
    }
}
