package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.OrderDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto;
import kr.co.nutrifit.nutrifit.backend.dto.OrderItemExcelDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Order;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getUserOrders(@AuthenticationPrincipal UserAdapter userAdapter,
                                                        Pageable pageable) {
        Page<OrderDto> orders = orderService.getOrdersByUser(userAdapter.getUser().getId(), pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderPaymentId}")
    public ResponseEntity<List<OrderDto>> getNonMemberOrder(@PathVariable String orderPaymentId) {
        return ResponseEntity.ok(orderService.getNonMemberOrder(orderPaymentId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Page<OrderDto>> getUserOrdersByAdmin(@AuthenticationPrincipal UserAdapter userAdapter,
                                                               @PathVariable Long id,
                                                               Pageable pageable) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<OrderDto> orders = orderService.getOrdersByUser(id, pageable);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<OrderDto>> getOrders(@AuthenticationPrincipal UserAdapter userAdapter,
                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                    Pageable pageable) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<OrderDto> orders = orderService.getOrders(pageable, startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/filter")
    public ResponseEntity<Page<OrderDto>> getOrdersByFilter(@AuthenticationPrincipal UserAdapter userAdapter,
                                                            @RequestParam String status,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                    Pageable pageable) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<OrderDto> orders = orderService.getOrdersByFilterBetweenDate(status, pageable, startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/excel/filter")
    public ResponseEntity<List<OrderItemExcelDto>> getOrdersForExcelByFilter(@AuthenticationPrincipal UserAdapter userAdapter,
                                                                    @RequestParam String status, @RequestParam int limit) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<OrderItemExcelDto> orders = orderService.getOrdersForExcelByFilter(status, limit);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/tracking")
    public ResponseEntity<?> updateTrackingNumber(@AuthenticationPrincipal UserAdapter userAdapter,
                                                  @RequestBody List<OrderItemExcelDto> dto) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            orderService.updateTrackingNumbers(dto);
            return ResponseEntity.ok("성공적으로 운송장번호가 업데이트 되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("운송장번호 업데이트에 실패했습니다.");
        }
    }
}
