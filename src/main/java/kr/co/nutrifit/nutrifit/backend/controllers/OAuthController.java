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

    @PutMapping("/username")
    public ResponseEntity<?> changeOAuthUsername(@RequestBody OAuthRequest request) {
        try {
            UserDto userDto = oAuthService.changeUsername(request.getEmail(), request.getUsername());
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("사용자 이메일 혹은 닉네임이 겹칩니다.");
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleOAuthCheckRegister(@RequestBody OAuthRequest request) {
        try {
            UserDto userDto = oAuthService.checkAndMakeGoogleUser(request.getCode());
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
    }


    @PostMapping("/naver")
    public ResponseEntity<?> naverOAuthCallback(@RequestBody OAuthRequest request) {
        try {
            UserDto userDto = oAuthService.checkAndMakeNaverUser(request.getCode());
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
    }

    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoOAuthCheckRegister(@RequestBody OAuthRequest request) {
        try {
            UserDto userDto = oAuthService.checkAndMakeKakaoUser(request.getCode());
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
    }
}
