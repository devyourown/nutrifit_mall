package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.GoogleUserDto;
import kr.co.nutrifit.nutrifit.backend.dto.NaverUserDto;
import kr.co.nutrifit.nutrifit.backend.dto.SignDto;
import kr.co.nutrifit.nutrifit.backend.dto.UserDto;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    @Value("${oauth.google.client-id}")
    private String googleClientId;
    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;
    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;
    @Value("${oauth.naver.client-id}")
    private String naverClientId;
    @Value("${oauth.naver.client-secret}")
    private String naverClientSecret;
    @Value("${oauth.naver.redirect-uri}")
    private String naverRedirectUri;

    private String getGoogleAccessToken(String code) {
        // Google OAuth 토큰 요청 URL
        String url = "https://oauth2.googleapis.com/token";

        // 요청에 필요한 파라미터
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", googleClientId);
        params.put("client_secret", googleClientSecret);
        params.put("redirect_uri", googleRedirectUri);
        params.put("grant_type", "authorization_code");

        ResponseEntity<Map> response = restTemplate.postForEntity(url, params, Map.class);
        return (String) response.getBody().get("access_token");
    }

    private GoogleUserDto getGoogleUser(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GoogleUserDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, GoogleUserDto.class);
        return response.getBody();
    }

    private User createUserFromGoogleUser(GoogleUserDto userDto) {
        User user = User.builder()
                .username("temporary" + UUID.randomUUID().toString().substring(0, 8))
                .email(userDto.getEmail())
                .isOAuth(true)
                .role(Role.ROLE_USER)
                .build();
        user.setImageUrl(userDto.getPicture());
        return userRepository.save(user);
    }

    @Transactional
    public UserDto checkAndMakeGoogleUser(String code) {
        String accessToken = getGoogleAccessToken(code);
        GoogleUserDto googleUser = getGoogleUser(accessToken);
        User user = null;
        if (userRepository.existsByEmail(googleUser.getEmail()))
            user = userRepository.findByEmail(googleUser.getEmail())
                    .orElseThrow();
        else
            user = createUserFromGoogleUser(googleUser);
        String jwt = tokenProvider.generateToken(user);
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .token(jwt)
                .role(user.getRole())
                .username(user.getUsername())
                .build();
    }

    @Transactional
    public UserDto makeGoogleUsername(String email, String username) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Wrong Email"));
        user.setUsername(username);
        userRepository.save(user);
        String jwt = tokenProvider.generateToken(user);
        return UserDto.builder()
                .id(user.getId())
                .token(jwt)
                .role(user.getRole())
                .username(user.getUsername())
                .build();
    }

    private String getNaverAccessToken(String code) {
        // Google OAuth 토큰 요청 URL
        String url = "https://nid.naver.com/oauth2.0/token";

        // 요청에 필요한 파라미터
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", naverClientId);
        params.put("client_secret", naverClientSecret);
        params.put("redirect_uri", naverRedirectUri);
        params.put("grant_type", "authorization_code");

        ResponseEntity<Map> response = restTemplate.postForEntity(url, params, Map.class);
        return (String) response.getBody().get("access_token");
    }

    private NaverUserDto getNaverUser(String accessToken) {
        String url = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<NaverUserDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, NaverUserDto.class);

        return response.getBody();
    }

    @Transactional
    private User createUserFromNaverUser(NaverUserDto userDto) throws Exception {
        User user = User.builder()
                .email(userDto.getEmail())
                .username("temporary" + UUID.randomUUID().toString().substring(0, 8))
                .isOAuth(true)
                .build();
        user.setImageUrl(userDto.getProfile_image());
        return user;
    }

    public UserDto checkAndMakeNaverUser(String code) throws Exception {
        String accessToken = getNaverAccessToken(code);
        NaverUserDto naverUser = getNaverUser(accessToken);

        User user = userRepository.findByEmail(naverUser.getEmail())
                .orElse(createUserFromNaverUser(naverUser));
        String jwt = tokenProvider.generateToken(user);
        return UserDto.builder()
                .id(user.getId())
                .token(jwt)
                .role(user.getRole())
                .username(user.getUsername())
                .build();
    }
}
