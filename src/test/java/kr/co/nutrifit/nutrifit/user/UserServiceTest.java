package kr.co.nutrifit.nutrifit.user;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import kr.co.nutrifit.nutrifit.backend.dto.SignDto;
import kr.co.nutrifit.nutrifit.backend.dto.UserDto;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void registerUser_Success() throws Exception {
        // Given
        SignDto signDto = new SignDto("test@example.com", "testuser", "password123!");

        when(userRepository.existsByUsername(signDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(signDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User user = userService.registerUser(UserDto.builder()
                .email(signDto.getEmail())
                .password(signDto.getPassword())
                .build());

        // Then
        assertNotNull(user);
        assertEquals(signDto.getUsername(), user.getUsername());
        assertEquals(signDto.getEmail(), user.getEmail());
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void registerUser_UsernameOrEmailAlreadyTaken() {
        // Given
        SignDto signDto = new SignDto("test@example.com", "testuser", "password123!");

        when(userRepository.existsByUsername(signDto.getUsername())).thenReturn(true);
        when(userRepository.existsByEmail(signDto.getEmail())).thenReturn(false);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(UserDto.builder()
                    .email(signDto.getEmail())
                    .password(signDto.getPassword())
                    .build());
        });

        assertEquals("이메일 혹은 닉네임이 이미 사용중입니다.", exception.getMessage());
    }

    @Test
    void registerUser_EmailAlreadyRegistered() {
        // Given
        SignDto signDto = new SignDto("test@example.com", "testuser", "password123!");

        when(userRepository.existsByUsername(signDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(signDto.getEmail())).thenReturn(true);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(UserDto.builder()
                    .email(signDto.getEmail())
                    .password(signDto.getPassword())
                    .build());
        });

        assertEquals("이메일 혹은 닉네임이 이미 사용중입니다.", exception.getMessage());
    }

    @Test
    void registerUser_InvalidEmailFormat() {
        // Given
        SignDto signDto = new SignDto("invalid-email", "testuser", "password123!");

        // When
        Set<ConstraintViolation<SignDto>> violations = validator.validate(signDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("이메일은 유효해야 합니다.")));
    }

    @Test
    void registerUser_InvalidUsernameFormat() {
        // Given
        SignDto signDto = new SignDto("test@example.com", "invalid username!", "password123!");

        // When
        Set<ConstraintViolation<SignDto>> violations = validator.validate(signDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("닉네임은 한글, 숫자, 영문만 입력 가능합니다.")));
    }

    @Test
    void registerUser_InvalidPasswordFormat() {
        // Given
        SignDto signDto = new SignDto("test@example.com", "testuser", "password");

        // When
        Set<ConstraintViolation<SignDto>> violations = validator.validate(signDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("비밀번호는 8글자 이상으로 영문, 특수문자, 숫자가 포함되어야 합니다.")));
    }
}
