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

    @Column(columnDefinition = "text[]")
    private List<String> shippingDetails;

    @Column(columnDefinition = "text[]")
    private List<String> exchangeAndReturns;

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
