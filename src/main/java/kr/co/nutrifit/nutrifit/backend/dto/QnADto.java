package kr.co.nutrifit.nutrifit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QnADto {
    public QnADto(Long id, String productName, String question, LocalDateTime questionDate, String answer, LocalDateTime answerDate) {
        this.id = id;
        this.productName = productName;
        this.question = question;
        this.questionDate = questionDate;
        this.answer = answer;
        this.answerDate = answerDate;
    }
    private Long id;
    private Long productId;
    private String productName;
    private String question;
    private LocalDateTime questionDate;
    private String answer;
    private LocalDateTime answerDate;
}
