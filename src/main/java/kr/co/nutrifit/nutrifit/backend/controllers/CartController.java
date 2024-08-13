package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.persistence.entities.CartItem;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addItemToCart(
            @AuthenticationPrincipal UserAdapter user,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        cartService.addItemToCart(user.getUsername(), productId, quantity);
        return ResponseEntity.ok("상품이 장바구니에 추가되었습니다.");
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems(@AuthenticationPrincipal UserAdapter user) {
        List<CartItem> cartItems = cartService.getCartItems(user.getUsername());
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCartItemQuantity(
            @AuthenticationPrincipal UserAdapter user,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        Long cartId = user.getUser().getCart().getId();
        cartService.updateItemQuantity(cartId, productId, quantity);
        return ResponseEntity.ok("아이템 수량이 변경되었습니다.");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItemFromCart(
            @AuthenticationPrincipal UserAdapter user,
            @RequestParam Long productId
    ) {
        cartService.removeItemFromCart(user.getUsername(), productId);
        return ResponseEntity.ok("상품이 장바구니에서 제거되었습니다.");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal UserAdapter user) {
        cartService.clearCart(user.getUsername());
        return ResponseEntity.ok("장바구니가 비워졌습니다.");
    }
}
