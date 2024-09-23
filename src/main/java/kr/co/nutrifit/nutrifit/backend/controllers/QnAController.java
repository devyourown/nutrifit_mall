package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.QnADto;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.QnAService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnAController {
    private final QnAService qnAService;

    @PostMapping
    public ResponseEntity<?> createQnA(@AuthenticationPrincipal UserAdapter userAdapter,
                                       @RequestBody QnADto qna) {
        try {
            qnAService.createQnA(qna.getProductId(), userAdapter.getUser(), qna.getQuestion());
            return ResponseEntity.ok("질문이 등록 되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Page<QnADto>> getQnaByUser(@AuthenticationPrincipal UserAdapter userAdapter,
                                                     Pageable pageable) {
        try {
            Page<QnADto> result = qnAService.getUserQna(userAdapter.getUser(), pageable);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserQna(@AuthenticationPrincipal UserAdapter userAdapter,
                                                @PathVariable Long id) {
        try {
            boolean deleted = qnAService.deleteUserQna(id, userAdapter.getUser());

            if (deleted) {
                return ResponseEntity.ok("Q&A가 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제할 권한이 없습니다.");
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 Q&A를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Q&A 삭제 중 오류가 발생했습니다.");
        }
    }


}
