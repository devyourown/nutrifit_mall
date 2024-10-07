package kr.co.nutrifit.nutrifit.backend.controllers;

import jakarta.validation.Valid;
import kr.co.nutrifit.nutrifit.backend.dto.CouponDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Coupon;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.CouponService;
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
    public ResponseEntity<String> assignCouponToUser(@RequestBody String code,
                                                     @AuthenticationPrincipal UserAdapter userAdapter) {
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            couponService.assignCouponToUser(code, userAdapter.getUser());
            return ResponseEntity.ok("쿠폰이 정상적으로 할당 되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<CouponDto>> getUserCoupon(@AuthenticationPrincipal UserAdapter userAdapter,
                                                         Pageable pageable) {
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<CouponDto> coupons = couponService.getUserCoupon(userAdapter.getUser().getId(), pageable);
        return ResponseEntity.ok(coupons);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{userId}")
    public ResponseEntity<Page<CouponDto>> getUserCouponsByAdmin(@AuthenticationPrincipal UserAdapter userAdapter,
                                                                 @PathVariable Long userId,
                                                                 Pageable pageable) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<CouponDto> coupons = couponService.getUserCoupon(userId, pageable);
        return ResponseEntity.ok(coupons);
    }
}
