package kr.co.nutrifit.nutrifit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable {
    public ProductDto(Long id, String name, String description, String category, int stockQuantity, int lowStockThreshold,
                      List<String> imageUrls, List<String> badgeTexts, Long originalPrice, Long discountedPrice,
                      Long reviewRating, Long reviewCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.lowStockThreshold = lowStockThreshold;
        this.imageUrls = imageUrls;
        this.badgeTexts = badgeTexts;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.reviewRating = reviewRating;
        this.reviewCount = reviewCount;
    }

    public ProductDto(Long id, String name, String description, String category, int stockQuantity, int lowStockThreshold,
                      List<String> imageUrls, List<String> badgeTexts, Long originalPrice, Long discountedPrice,
                      Long reviewRating, Long reviewCount, boolean isReleased) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.lowStockThreshold = lowStockThreshold;
        this.imageUrls = imageUrls;
        this.badgeTexts = badgeTexts;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.reviewRating = reviewRating;
        this.reviewCount = reviewCount;
        this.isReleased = isReleased;
    }
    private Long id;
    private String name;
    private String description;
    private String category;
    private int stockQuantity;
    private int lowStockThreshold;
    private List<String> imageUrls;
    private List<String> badgeTexts;
    private Long originalPrice;
    private Long discountedPrice;
    private Long reviewRating;
    private Long reviewCount;
    private List<OptionDto> options;
    private ProductDetailDto productDetailDto;
    private boolean isReleased;
}
