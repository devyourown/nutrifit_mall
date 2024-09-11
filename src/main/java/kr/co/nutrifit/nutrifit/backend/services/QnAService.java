package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.persistence.ProductDetailRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.QnARepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ProductDetail;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ProductQnA;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QnAService {
    private final QnARepository qnARepository;
    private final ProductDetailRepository detailRepository;

    public void createQnA(Long productId, User user, String question) {
        ProductDetail productDetail = detailRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("No product with this ID."));
        ProductQnA qnA = ProductQnA
                .builder()
                .productDetail(productDetail)
                .question(question)
                .questionDate(LocalDateTime.now())
                .user(user)
                .build();
        qnARepository.save(qnA);
    }
}
