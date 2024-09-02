package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.*;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Options;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ProductDetail;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ProductQnA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public void addProduct(ProductDto productDto) {
        ProductDetailDto detailDto = productDto.getProductDetailDto();
        ProductDetail productDetail = ProductDetail.builder()
                .detailImageUrls(detailDto.getDetailImageUrls())
                .shippingFee(detailDto.getShippingFee())
                .shippingMethod(detailDto.getShippingMethod())
                .shippingDuration(detailDto.getShippingDuration())
                .bundleShippingAvailability(detailDto.getBundleShippingAvailability())
                .exchangeAndReturnFee(detailDto.getExchangeAndReturnFee())
                .exchangeAndReturnPeriod(detailDto.getExchangeAndReturnPeriod())
                .exchangeAndReturnPolicy(detailDto.getExchangeAndReturnPolicy())
                .qnas(new ArrayList<>())
                .build();
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
                .reviewRating(productDto.getReviewRating())
                .reviewCount(productDto.getReviewCount())
                .productDetail(productDetail)
                .build();
        productDto.getOptions().forEach(optionDto -> product.addOption(Options.builder()
                .quantity(optionDto.getQuantity())
                .price(optionDto.getPrice())
                .description(optionDto.getDescription())
                .build()));
        Product saved = productRepository.save(product);
        productDetail.setProduct(saved);
    }

    @Transactional
    public void updateProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        if (productDto.getProductDetailDto() != null) {
            ProductDetailDto detailDto = productDto.getProductDetailDto();
            ProductDetail productDetail = ProductDetail.builder()
                    .detailImageUrls(detailDto.getDetailImageUrls())
                    .shippingFee(detailDto.getShippingFee())
                    .shippingMethod(detailDto.getShippingMethod())
                    .shippingDuration(detailDto.getShippingDuration())
                    .bundleShippingAvailability(detailDto.getBundleShippingAvailability())
                    .exchangeAndReturnFee(detailDto.getExchangeAndReturnFee())
                    .exchangeAndReturnPeriod(detailDto.getExchangeAndReturnPeriod())
                    .exchangeAndReturnPolicy(detailDto.getExchangeAndReturnPolicy())
                    .qnas(new ArrayList<>())
                    .build();
            product.setProductDetail(productDetail);
        }
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setBadgeTexts(productDto.getBadgeTexts());
        product.setOriginalPrice(productDto.getOriginalPrice());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setImageUrls(productDto.getImageUrls());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setCategory(product.getCategory());
        product.setLowStockThreshold(product.getLowStockThreshold());
        productDto.getOptions().forEach(optionDto -> product.addOption(Options.builder()
                .quantity(optionDto.getQuantity())
                .price(optionDto.getPrice())
                .description(optionDto.getDescription())
                .build()));
        product.setReviewCount(productDto.getReviewCount());
        product.setReviewRating(product.getReviewRating());
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

    public List<ProductDto> getAllProduct() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        ProductDetail productDetail = product.getProductDetail();
        ProductDto productDto = convertToDto(product);
        productDto.setProductDetailDto(ProductDetailDto.builder()
                .detailImageUrls(productDetail.getDetailImageUrls())
                .qnas(productDetail.getQnas().stream().map(this::convertToQnADto).toList())
                .shippingFee(productDetail.getShippingFee())
                .shippingDuration(productDetail.getShippingDuration())
                .exchangeAndReturnFee(productDetail.getExchangeAndReturnFee())
                .exchangeAndReturnPeriod(productDetail.getExchangeAndReturnPeriod())
                .exchangeAndReturnPolicy(productDetail.getExchangeAndReturnPolicy())
                .shippingMethod(productDetail.getShippingMethod())
                .bundleShippingAvailability(productDetail.getBundleShippingAvailability())
                .build());
        return productDto;
    }

    public List<ProductDto> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream().map(this::convertToDto)
                .toList();
    }

    private ProductQnADto convertToQnADto(ProductQnA qna) {
        return ProductQnADto.builder()
                .answer(qna.getAnswer())
                .question(qna.getQuestion())
                .answerDate(qna.getAnswerDate())
                .questionDate(qna.getQuestionDate())
                .build();
    }

    private ProductDto convertToDto(Product product) {
        List<OptionDto> optionDtos = product.getOptions().stream()
                .map(this::convertOptionToDto)
                .toList();
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
                .reviewCount(product.getReviewCount())
                .reviewRating(product.getReviewRating())
                .options(optionDtos)
                .build();
    }

    private OptionDto convertOptionToDto(Options options) {
        return OptionDto.builder()
                .id(options.getId())
                .quantity(options.getQuantity())
                .price(options.getPrice())
                .description(options.getDescription())
                .build();
    }
}
