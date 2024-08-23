package kr.co.nutrifit.nutrifit.backend.controllers;

import kr.co.nutrifit.nutrifit.backend.dto.OAuthRequest;
import kr.co.nutrifit.nutrifit.backend.dto.UserDto;
import kr.co.nutrifit.nutrifit.backend.services.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    @PostMapping("/google")
    public ResponseEntity<?> googleOAuthCallback(@RequestBody OAuthRequest request) {
        try {
            UserDto userDto = oAuthService.makeGoogleUsername(request.getEmail(), request.getUsername());
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("사용자 이메일 혹은 닉네임이 겹칩니다.");
        }
    }

    @PostMapping("/google/check")
    public ResponseEntity<?> googleOAuthCheckRegister(@RequestBody OAuthRequest request) {
        try {
            UserDto userDto = oAuthService.checkAndMakeGoogleUser(request.getCode());
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
    }

    @PostMapping("/naver")
    public ResponseEntity<?> naverOAuthCallback(@RequestBody OAuthRequest request) {
        try {
            UserDto userDto = oAuthService.authenticationNaverUser(request.getCode(), request.getUsername());
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("사용자 이메일 혹은 닉네임이 겹칩니다.");
        }
    }
}
