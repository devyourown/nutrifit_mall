package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.*;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductDetailRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Options;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ProductDetail;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ProductQnA;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;

    @Transactional
    public void addProduct(ProductDto productDto) {
        ProductDetailDto detailDto = productDto.getProductDetailDto();
        ProductDetail productDetail = ProductDetail.builder()
                .detailImageUrls(detailDto.getDetailImageUrls())
                .shippingDetails(detailDto.getShippingDetails())
                .exchangeAndReturns(detailDto.getExchangeAndReturns())
                .qnas(new ArrayList<>())
                .build();
        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .originalPrice(productDto.getOriginalPrice())
                .badgeTexts(productDto.getBadgeTexts())
                .discountedPrice(productDto.getDiscountedPrice())
                .isReleased(productDto.isReleased())
                .stockQuantity(productDto.getStockQuantity())
                .category(productDto.getCategory())
                .imageUrls(productDto.getImageUrls())
                .lowStockThreshold(productDto.getLowStockThreshold())
                .reviewRating(0L)
                .reviewCount(0L)
                .build();
        productDto.getOptions().forEach(optionDto -> product.addOption(Options.builder()
                .quantity(optionDto.getQuantity())
                .price(optionDto.getPrice())
                .description(optionDto.getDescription())
                .build()));
        Product saved = productRepository.save(product);
        productDetail.setProduct(saved);
        productDetailRepository.save(productDetail);
    }

    @Transactional
    public void updateProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        if (productDto.getProductDetailDto() != null) {
            ProductDetailDto detailDto = productDto.getProductDetailDto();
            ProductDetail productDetail = productDetailRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new IllegalArgumentException("상품 상세정보를 찾을 수 없습니다."));
            productDetail.setDetailImageUrls(detailDto.getDetailImageUrls());
            productDetail.setShippingDetails(detailDto.getShippingDetails());
            productDetail.setExchangeAndReturns(detailDto.getExchangeAndReturns());
        }
        product.getOptions().clear();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setBadgeTexts(productDto.getBadgeTexts());
        product.setOriginalPrice(productDto.getOriginalPrice());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setImageUrls(productDto.getImageUrls());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setCategory(productDto.getCategory());
        product.setReleased(productDto.isReleased());
        product.setLowStockThreshold(productDto.getLowStockThreshold());
        productDto.getOptions().forEach(optionDto -> product.addOption(Options.builder()
                .quantity(optionDto.getQuantity())
                .price(optionDto.getPrice())
                .description(optionDto.getDescription())
                .build()));
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        productRepository.delete(product);
    }

    @Transactional
    public void reduceStock(List<CartItemDto> items) {
        List<Product> savedProduct = new ArrayList<>();
        items.forEach(item -> {
            Product product = productRepository.findById(item.getId())
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

    public Page<ProductDto> getAllProduct(Pageable pageable) {
        return productRepository.findAllToDto(pageable);
    }

    public Page<ProductDto> getReleasedProducts(Pageable pageable) {
        return productRepository.findReleasedProducts(pageable);
    }

    public ProductDto getProductById(Long productId) {
        ProductDto productDto = productRepository.findProductDtoById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        List<OptionDto> optionDtos = productRepository.findOptionsByProductId(productId);
        ProductDetailDto productDetailDto = productDetailRepository.findProductDetailDtoByProductId(productId);
        List<ProductQnADto> qnas = productDetailRepository.findProductQnADtoByProductDetailId(productDetailDto.getId());
        productDetailDto.setQnas(qnas);
        productDto.setOptions(optionDtos);
        productDto.setProductDetailDto(productDetailDto);
        return productDto;
    }

    public Page<ProductDto> getProductsByCategory(Pageable pageable, String category) {
        return productRepository.findProductsByCategory(pageable, category);
    }
}
