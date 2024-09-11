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
    private List<String> detailImageUrls;
    private List<ProductQnADto> qnas;
    private List<String> shippingDetails;
    private List<String> exchangeAndReturns;
}
