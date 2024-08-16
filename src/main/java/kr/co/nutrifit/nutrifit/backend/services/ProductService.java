package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.ProductDto;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .price(productDto.getPrice())
                .stockQuantity(productDto.getStockQuantity())
                .category(productDto.getCategory())
                .build();
        productRepository.save(product);
    }

    @Transactional
    public void updateProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setCategory(product.getCategory());
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        productRepository.delete(product);
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
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStockQuantity(),
                product.getLowStockThreshold(),
                product.getImageUrl()
        );
    }
}
