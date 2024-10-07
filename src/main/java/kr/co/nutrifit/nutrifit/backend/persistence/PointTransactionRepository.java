package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.PointTransactionDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Point;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.PointTransactionDto(" +
            "pt.transactionType, " +
            "pt.description, " +
            "pt.createdAt, " +
            "pt.points) " +
            "FROM PointTransaction pt " +
            "WHERE pt.user.id = :userId")
    Page<PointTransactionDto> findByUserId(Long userId, Pageable pageable);
}
