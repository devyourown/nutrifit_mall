package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.QnADto;
import kr.co.nutrifit.nutrifit.backend.persistence.ProductDetailRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.QnARepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ProductDetail;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ProductQnA;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

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

    public Page<QnADto> getUserQna(User user, Pageable pageable) {
        return qnARepository.findByUserWithDto(user, pageable);
    }

    @Transactional
    public boolean deleteUserQna(Long id, User user) {
        ProductQnA qna = qnARepository.findByIdWithUser(id)
                .orElseThrow(() -> new NoSuchElementException("Q&A를 찾을 수 없습니다."));

        if (!qna.getUser().getId().equals(user.getId())) {
            return false;
        }

        qnARepository.delete(qna);
        return true;
    }
}
