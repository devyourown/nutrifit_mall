package kr.co.nutrifit.nutrifit.backend.controllers;

import jakarta.validation.Valid;
import kr.co.nutrifit.nutrifit.backend.dto.SignDto;
import kr.co.nutrifit.nutrifit.backend.dto.UserDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.security.JwtTokenProvider;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody SignDto signDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signDto.getEmail(),
                            signDto.getPassword()
                    )
            );
            UserAdapter userAdapter = (UserAdapter) authentication.getPrincipal();
            User user = userAdapter.getUser();
            String jwt = tokenProvider.generateToken(user);
            return ResponseEntity.ok(UserDto.builder()
                    .id(user.getId())
                    .token(jwt)
                    .role(user.getRole())
                    .username(user.getUsername())
                    .build());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).header(HttpHeaders.CONTENT_TYPE,
                            MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8)
                    .body("이메일과 비밀번호가 일치하지 않습니다.");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody @Valid SignDto signDto) {
        try {
            userService.registerUser(signDto);
            return ResponseEntity.status(201).header(HttpHeaders.CONTENT_TYPE,
                    MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8)
                    .body("사용자가 등록 되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(400).header(HttpHeaders.CONTENT_TYPE,
                    MediaType.TEXT_PLAIN_VALUE + ";charset=" + StandardCharsets.UTF_8)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        try {
            userService.resetPassword(email);
            return ResponseEntity.ok("임시 비밀번호가 이메일로 발송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
