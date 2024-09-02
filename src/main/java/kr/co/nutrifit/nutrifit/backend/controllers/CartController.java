package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.CartItem;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
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
            return ResponseEntity.badRequest().header(HttpHeaders.CONTENT_TYPE,
                    MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8)
                    .body("수량은 1 이상이어야 합니다.");
        }
        cartService.addItemToCart(user.getUser(), productId, quantity);
        return ResponseEntity.status(201).header(HttpHeaders.CONTENT_TYPE,
                MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8)
                .body("상품이 장바구니에 추가되었습니다.");
    }

    @PostMapping
    public ResponseEntity<String> changeCart(@AuthenticationPrincipal UserAdapter user,
                                             @RequestBody List<CartItemDto> items) {
        try {
            cartService.updateCart(user.getUser(), items);
            return ResponseEntity.ok("장바구니가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItemDto>> getCartItems(@AuthenticationPrincipal UserAdapter user) {
        List<CartItemDto> cartItems = cartService.getCartItems(user.getUser());
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("/items")
    public ResponseEntity<String> updateCartItemQuantity(
            @AuthenticationPrincipal UserAdapter user,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        if (quantity <= 0) {
            return ResponseEntity.badRequest().header(HttpHeaders.CONTENT_TYPE,
                    MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8)
                    .body("수량은 1 이상이어야 합니다.");
        }
        cartService.updateItemQuantity(user.getUser(), productId, quantity);
        return ResponseEntity.status(200).header(HttpHeaders.CONTENT_TYPE,
                MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8)
                .body("아이템 수량이 변경되었습니다.");
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<String> removeItemFromCart(
            @AuthenticationPrincipal UserAdapter user,
            @PathVariable Long productId
    ) {
        cartService.removeItemFromCart(user.getUser(), productId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/items")
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal UserAdapter user) {
        cartService.clearCart(user.getUser());
        return ResponseEntity.noContent().build();
    }
}
