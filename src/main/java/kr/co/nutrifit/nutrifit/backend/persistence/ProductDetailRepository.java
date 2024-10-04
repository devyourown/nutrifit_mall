package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.ProductDetailDto;
import kr.co.nutrifit.nutrifit.backend.dto.ProductQnADto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    Optional<ProductDetail> findByProductId(Long productId);
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.ProductDetailDto(" +
            "pd.detailImageUrls, pd.shippingDetails, pd.exchangeAndReturns) " +
            "FROM ProductDetail pd WHERE pd.product.id = :productId")
    ProductDetailDto findProductDetailDtoByProductId(@Param("productId") Long productId);

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.ProductQnADto(q.question, q.answer, q.questionDate, q.answerDate) " +
            "FROM ProductQnA q WHERE q.productDetail.id = :productDetailId")
    List<ProductQnADto> findProductQnADtoByProductDetailId(@Param("productDetailId") Long productDetailId);

}
