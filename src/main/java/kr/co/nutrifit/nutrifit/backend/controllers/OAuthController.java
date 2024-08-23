package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.UserDto;
import kr.co.nutrifit.nutrifit.backend.services.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    @PostMapping("/google")
    public ResponseEntity<?> googleOAuthCallback(@RequestParam String code) {
        try {
            UserDto userDto = oAuthService.authenticationGoogleUser(code);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("사용자 이메일 혹은 닉네임이 겹칩니다.");
        }
    }

    @PostMapping("/naver")
    public ResponseEntity<?> naverOAuthCallback(@RequestParam String code) {
        try {
            UserDto userDto = oAuthService.authenticationNaverUser(code);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(409).body("사용자 이메일 혹은 닉네임이 겹칩니다.");
        }
    }
}
