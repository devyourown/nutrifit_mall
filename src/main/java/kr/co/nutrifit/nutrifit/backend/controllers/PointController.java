package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.PointDto;
import kr.co.nutrifit.nutrifit.backend.dto.PointTransactionDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;

    @GetMapping
    public ResponseEntity<PointDto> getUserPoints(@AuthenticationPrincipal UserAdapter userAdapter) {
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        PointDto point = pointService.getUserPoints(userAdapter.getUser().getId());
        return ResponseEntity.ok(point);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{userId}")
    public ResponseEntity<PointDto> getUserPointsByAdmin(@AuthenticationPrincipal UserAdapter userAdapter,
                                                         @PathVariable Long userId) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        PointDto point = pointService.getUserPoints(userId);
        return ResponseEntity.ok(point);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/transactions/{userId}")
    public ResponseEntity<Page<PointTransactionDto>> getTransactionsByAdmin(@AuthenticationPrincipal UserAdapter userAdapter,
                                                                      @PathVariable Long userId,
                                                                      Pageable pageable) {
        if (!userAdapter.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<PointTransactionDto> transactions = pointService.getTransactions(userId, pageable);
        return ResponseEntity.ok(transactions);
    }
}
