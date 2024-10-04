package kr.co.nutrifit.nutrifit.backend.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDto {
    public ProductDetailDto(Long id, List<String> detailImageUrls, List<String> shippingDetails, List<String> exchangeAndReturns) {
        this.id = id;
        this.detailImageUrls = detailImageUrls;
        this.shippingDetails = shippingDetails;
        this.exchangeAndReturns = exchangeAndReturns;
    }
    public ProductDetailDto(List<String> detailImageUrls, List<String> shippingDetails, List<String> exchangeAndReturns) {
        this.detailImageUrls = detailImageUrls;
        this.shippingDetails = shippingDetails;
        this.exchangeAndReturns = exchangeAndReturns;
    }
    private Long id;
    private List<String> detailImageUrls;
    private List<ProductQnADto> qnas;
    private List<String> shippingDetails;
    private List<String> exchangeAndReturns;
}
