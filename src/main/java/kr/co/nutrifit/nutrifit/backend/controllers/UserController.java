package kr.co.nutrifit.nutrifit.backend.controllers;

import jakarta.validation.Valid;
import kr.co.nutrifit.nutrifit.backend.dto.JwtAuthenticationDto;
import kr.co.nutrifit.nutrifit.backend.dto.SignDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.security.JwtTokenProvider;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
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

            String jwt = tokenProvider.generateToken((UserAdapter) authentication.getPrincipal());
            return ResponseEntity.ok(new JwtAuthenticationDto(jwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("이메일과 비밀번호가 일치하지 않습니다.");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody @Valid SignDto signDto) {
        try {
            userService.registerUser(signDto);
            return ResponseEntity.ok("사용자가 등록 되었습니다.");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(e.getMessage());
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
