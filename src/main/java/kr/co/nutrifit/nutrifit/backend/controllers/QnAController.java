package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.QnADto;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.QnAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
