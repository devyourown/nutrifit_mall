package kr.co.nutrifit.nutrifit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
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
}
