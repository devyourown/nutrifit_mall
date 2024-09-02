package kr.co.nutrifit.nutrifit.backend.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(columnDefinition = "text[]")
    private List<String> detailImageUrls;

    @OneToMany(mappedBy = "productDetail", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ProductQnA> qnas;

    @Column(columnDefinition = "text")
    private String shippingMethod; // 배송방법

    @Column(columnDefinition = "text")
    private String bundleShippingAvailability; // 묶음배송 여부

    @Column(columnDefinition = "text")
    private String shippingFee; // 배송비

    @Column(columnDefinition = "text")
    private String shippingDuration; // 배송기간

    @Column(columnDefinition = "text")
    private String exchangeAndReturnPolicy; // 교환/반품 안내

    @Column(columnDefinition = "text")
    private String exchangeAndReturnFee; // 교환/반품 비용

    @Column(columnDefinition = "text")
    private String exchangeAndReturnPeriod; // 교환/반품 신청 기준일

    public void addQna(ProductQnA qna) {
        if (qnas == null) {
            qnas = new ArrayList<>();
        }
        qna.setProductDetail(this);
        qnas.add(qna);
    }

    public void removeQna(ProductQnA qna) {
        if (qnas != null) {
            qnas.remove(qna);
            qna.setProductDetail(null);
        }
    }
}
