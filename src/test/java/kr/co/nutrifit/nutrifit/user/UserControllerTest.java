package kr.co.nutrifit.nutrifit.user;

import kr.co.nutrifit.nutrifit.backend.controllers.UserController;
import kr.co.nutrifit.nutrifit.backend.dto.SignDto;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.security.JwtTokenProvider;
import kr.co.nutrifit.nutrifit.backend.security.UserAdapter;
import kr.co.nutrifit.nutrifit.backend.services.UserService;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



public class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void authenticateUser_Success() throws Exception {
        // Given
        SignDto signDto = new SignDto("test@example.com", "testuser", "password123!");

        // 가상의 유저 생성
        User user = User.builder()
                .username(signDto.getUsername())
                .email(signDto.getEmail())
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        UserAdapter userAdapter = new UserAdapter(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userAdapter, null, userAdapter.getAuthorities());

        // Mocking: AuthenticationManager와 JwtTokenProvider의 동작을 시뮬레이션
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenProvider.generateToken(any(UserAdapter.class))).thenReturn("jwt-token");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"email\": \"test@example.com\", \"password\": \"password123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void authenticateUser_Failure() throws Exception {
        // Given
        SignDto signDto = new SignDto("test@example.com", "testuser", "wrongPassword");

        // 특정 예외 클래스를 지정해 예외를 발생시킴
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"email\": \"test@example.com\", \"password\": \"wrongPassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("이메일과 비밀번호가 일치하지 않습니다."));
    }

    @Test
    void registerUser_Success() throws Exception {
        // Given
        SignDto signDto = new SignDto("test@example.com", "testuser", "password123!");

        when(userService.registerUser(any(SignDto.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"email\": \"test@example.com\", \"username\": \"testuser\", \"password\": \"password123!\"}"))
                .andExpect(status().is(201))
                .andExpect(content().string("사용자가 등록 되었습니다."));
    }

    @Test
    void registerUser_Failure() throws Exception {
        // Given
        SignDto signDto = new SignDto("test@example.com", "testuser", "password123!");

        when(userService.registerUser(any(SignDto.class))).thenThrow(new IllegalArgumentException("이메일 혹은 닉네임이 이미 사용중입니다."));

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@example.com\", \"username\": \"testuser\", \"password\": \"password123!\"}"))
                .andExpect(status().isBadRequest())  // 상태 코드 400을 기대하도록 수정
                .andExpect(content().string("이메일 혹은 닉네임이 이미 사용중입니다."));
    }

    @Test
    void registerUser_ValidationFailure() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"invalid-email\", \"username\": \"\", \"password\": \"pass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("이메일은 유효해야 합니다."))
                .andExpect(jsonPath("$.username").value("닉네임을 채워 주세요."))
                .andExpect(jsonPath("$.password").value("비밀번호는 8글자 이상으로 영문, 특수문자, 숫자가 포함되어야 합니다."));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"invalid@@email\", \"username\": \"@@!!\", \"password\": \"passWo!\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("이메일은 유효해야 합니다."))
                .andExpect(jsonPath("$.username").value("닉네임은 한글, 숫자, 영문만 입력 가능합니다."))
                .andExpect(jsonPath("$.password").value("비밀번호는 8글자 이상으로 영문, 특수문자, 숫자가 포함되어야 합니다."));
    }
}
