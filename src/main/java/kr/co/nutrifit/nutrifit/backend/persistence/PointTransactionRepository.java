package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.dto.PointTransactionDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Point;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    @Query("SELECT new kr.co.nutrifit.nutrifit.backend.dto.PointTransactionDto(" +
            "t.transactionType, t.description, t.createdAt, t.points) " +
            "FROM PointTransaction t " +
            "WHERE t.point = :point")
    List<PointTransactionDto> findByPoint(@Param("point") Point point);
}
