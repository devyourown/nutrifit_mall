package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.QnADto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.ProductQnA;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QnARepository extends JpaRepository<ProductQnA, Long> {
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.QnADto(" +
            "q.id, " +
            "p.name, " +
            "q.question, " +
            "q.questionDate, " +
            "q.answer, " +
            "q.answerDate) " +
            "FROM ProductQnA q " +
            "JOIN q.productDetail pd " +
            "JOIN pd.product p " +
            "WHERE q.user.id = :userId")
    Page<QnADto> findByUserWithDto(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT qna FROM ProductQnA qna JOIN FETCH qna.user WHERE qna.id = :qnaId")
    Optional<ProductQnA> findByIdWithUser(@Param("qnaId") Long qnaId);

}
