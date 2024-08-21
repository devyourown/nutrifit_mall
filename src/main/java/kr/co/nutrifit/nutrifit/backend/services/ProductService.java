package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.OrderItemDto;
import kr.co.nutrifit.nutrifit.backend.dto.ProductDto;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public void addProduct(ProductDto productDto) {
        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .originalPrice(productDto.getOriginalPrice())
                .badgeTexts(productDto.getBadgeTexts())
                .discountedPrice(productDto.getDiscountedPrice())
                .stockQuantity(productDto.getStockQuantity())
                .category(productDto.getCategory())
                .imageUrls(productDto.getImageUrls())
                .lowStockThreshold(productDto.getLowStockThreshold())
                .build();
        productRepository.save(product);
    }

    @Transactional
    public void updateProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setBadgeTexts(productDto.getBadgeTexts());
        product.setOriginalPrice(productDto.getOriginalPrice());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setImageUrls(productDto.getImageUrls());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setCategory(product.getCategory());
        product.setLowStockThreshold(product.getLowStockThreshold());
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        productRepository.delete(product);
    }

    @Transactional
    public void reduceStock(List<OrderItemDto> items) {
        List<Product> savedProduct = new ArrayList<>();
        items.forEach(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("주문량이 재고를 초과합니다.");
            }
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            if (product.getLowStockThreshold() >= product.getStockQuantity()) {
                //알림
            }
            savedProduct.add(product);
        });
        productRepository.saveAll(savedProduct);
    }

    public List<ProductDto> getAllProduct() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return convertToDto(product);
    }

    public List<ProductDto> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream().map(this::convertToDto)
                .toList();
    }

    private ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
        .description(product.getDescription())
        .imageUrls(product.getImageUrls())
        .badgeTexts(product.getBadgeTexts())
        .discountedPrice(product.getDiscountedPrice())
        .originalPrice(product.getOriginalPrice())
        .category(product.getCategory())
        .stockQuantity(product.getStockQuantity())
        .lowStockThreshold(product.getLowStockThreshold())
                .build();
    }
}
