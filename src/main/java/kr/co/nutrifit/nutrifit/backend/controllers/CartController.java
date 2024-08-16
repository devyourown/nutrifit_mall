package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.CartItem;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<String> addItemToCart(
            @AuthenticationPrincipal UserAdapter user,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        if (quantity <= 0) {
            return ResponseEntity.badRequest().body("수량은 1 이상이어야 합니다.");
        }
        cartService.addItemToCart(user.getUsername(), productId, quantity);
        return ResponseEntity.status(201).body("상품이 장바구니에 추가되었습니다.");
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItemDto>> getCartItems(@AuthenticationPrincipal UserAdapter user) {
        List<CartItemDto> cartItems = cartService.getCartItems(user.getUsername());
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("/items")
    public ResponseEntity<String> updateCartItemQuantity(
            @AuthenticationPrincipal UserAdapter user,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        if (quantity <= 0) {
            return ResponseEntity.badRequest().body("수량은 1 이상이어야 합니다.");
        }
        cartService.updateItemQuantity(user.getUser(), productId, quantity);
        return ResponseEntity.ok("아이템 수량이 변경되었습니다.");
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<String> removeItemFromCart(
            @AuthenticationPrincipal UserAdapter user,
            @PathVariable Long productId
    ) {
        cartService.removeItemFromCart(user.getUsername(), productId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/items")
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal UserAdapter user) {
        cartService.clearCart(user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
