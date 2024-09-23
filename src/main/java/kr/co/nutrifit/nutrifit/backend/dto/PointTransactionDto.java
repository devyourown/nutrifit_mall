package kr.co.nutrifit.nutrifit.backend.dto;

import kr.co.nutrifit.nutrifit.backend.persistence.entities.PointTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointTransactionDto {
    private PointTransactionType type;
    private String description;
    private LocalDateTime whenToBurn;
    private Long point;
}
