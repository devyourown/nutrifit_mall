package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.CartItemDto;
import kr.co.nutrifit.nutrifit.backend.persistence.CartItemRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.CartItem;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public List<CartItemDto> getCartItems(User user) {
        return cartItemRepository.findByUserId(user.getId());
    }

    public void syncCartItems(User user, List<CartItemDto> items) {
        cartItemRepository.deleteByUserId(user.getId());

        List<Long> productIds = items.stream()
                .map(CartItemDto::getProductId)
                .toList();

        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        List<CartItem> newCartItems = items.stream().map(itemDto -> {
            Product product = productMap.get(itemDto.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("상품을 찾을 수 없습니다: " + itemDto.getProductId());
            }
            return CartItem.builder()
                    .user(user)  // User와 직접 연관된 CartItem
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .imageUrl(product.getImageUrls().get(0))
                    .build();
        }).toList();

        cartItemRepository.saveAll(newCartItems);
    }
}
