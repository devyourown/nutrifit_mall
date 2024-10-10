package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/items")
    public ResponseEntity<Page<CartItemDto>> getCartItems(@AuthenticationPrincipal UserAdapter userAdapter,
                                                          Pageable pageable) {
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Page<CartItemDto> cartItems = cartService.getCartItems(userAdapter.getUser(), pageable);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/change")
    public ResponseEntity<String> changeCartItems(@AuthenticationPrincipal UserAdapter userAdapter,
                                                  @RequestBody List<CartItemDto> items) {
        if (userAdapter == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("유효한 사용자 정보가 없습니다.");
        }
        cartService.syncCartItems(userAdapter.getUser(), items);
        return ResponseEntity.ok("장바구니가 성공적으로 동기화되었습니다.");
    }
}
