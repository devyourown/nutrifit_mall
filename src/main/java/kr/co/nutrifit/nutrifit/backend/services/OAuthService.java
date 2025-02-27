package kr.co.nutrifit.nutrifit.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.nutrifit.nutrifit.backend.dto.*;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
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
    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;
    @Value("${oauth.kakao.client-secret}")
    private String kakaoClientSecret;
    @Value("${oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Transactional
    public UserDto changeUsername(String email, String username) {
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

    private String getGoogleAccessToken(String code) throws Exception {
        String url = "https://oauth2.googleapis.com/token";

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", googleClientId);
        params.put("client_secret", googleClientSecret);
        params.put("redirect_uri", googleRedirectUri);
        params.put("grant_type", "authorization_code");

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, params, Map.class);
            return (String) response.getBody().get("access_token");
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new Exception("Failed to retrieve access token" + ex.getMessage());
        }
    }

    private GoogleUserDto getGoogleUser(String accessToken){
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GoogleUserDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, GoogleUserDto.class);
        return response.getBody();
    }

    private User createUser(String email, String imageUrl) throws Exception {
        UserDto user = UserDto.builder()
                .username("temporary" + UUID.randomUUID().toString().substring(0, 8))
                .email(email)
                .isOAuth(true)
                .role(Role.ROLE_USER)
                .password(UUID.randomUUID().toString().substring(0, 8))
                .profileImage(imageUrl)
                .build();
        return userService.registerUser(user);
    }

    public UserDto checkAndMakeGoogleUser(String code) throws Exception {
        String accessToken = getGoogleAccessToken(code);
        GoogleUserDto googleUser = getGoogleUser(accessToken);
        User user = userRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    try {
                        return createUser(googleUser.getEmail(), googleUser.getPicture());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        String jwt = tokenProvider.generateToken(user);
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .token(jwt)
                .role(user.getRole())
                .username(user.getUsername())
                .build();
    }

    private String getNaverAccessToken(String code) throws Exception {
        String url = "https://nid.naver.com/oauth2.0/token";

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", naverClientId)
                .queryParam("client_secret", naverClientSecret)
                .queryParam("redirect_uri", naverRedirectUri)
                .queryParam("code", code);

        String uri = uriBuilder.toUriString();


        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            return (String) response.getBody().get("access_token");
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new Exception("Failed to retrieve access token" + ex.getMessage());
        }
    }

    private NaverUserDto getNaverUser(String accessToken) throws Exception {
        String url = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String responseBody = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);

        String id = rootNode.path("response").path("id").asText();
        String email = rootNode.path("response").path("email").asText();
        String profileImage = rootNode.path("response").path("profile_image").asText();

        return NaverUserDto.builder()
                .id(id)
                .email(email)
                .profile_image(profileImage)
                .build();
    }

    public UserDto checkAndMakeNaverUser(String code) throws Exception {
        String accessToken = getNaverAccessToken(code);
        NaverUserDto naverUser = getNaverUser(accessToken);
        User user = userRepository.findByEmail(naverUser.getEmail())
                .orElseGet(() -> {
                    try {
                        return createUser(naverUser.getEmail(), naverUser.getProfile_image());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        String jwt = tokenProvider.generateToken(user);
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .token(jwt)
                .role(user.getRole())
                .username(user.getUsername())
                .build();
    }

    private String getKakaoAccessToken(String code) throws Exception {
        String url = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoClientSecret);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);


        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return (String) response.getBody().get("access_token");
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new Exception("Failed to retrieve access token" + ex.getMessage());
        }
    }

    private KakaoUserDto getKakaoUser(String accessToken) throws Exception {
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String responseBody = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);

        String profileImage = rootNode.path("properties").path("profile_image").asText();
        String email = rootNode.path("kakao_account").path("email").asText();
        return KakaoUserDto.builder()
                .email(email)
                .profile(profileImage)
                .build();
    }

    public UserDto checkAndMakeKakaoUser(String code) throws Exception {
        String accessToken = getKakaoAccessToken(code);
        KakaoUserDto kakaoUser = getKakaoUser(accessToken);
        User user = userRepository.findByEmail(kakaoUser.getEmail())
                .orElseGet(() -> {
                    try {
                        return createUser(kakaoUser.getEmail(), kakaoUser.getProfile());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        String jwt = tokenProvider.generateToken(user);
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .token(jwt)
                .role(user.getRole())
                .username(user.getUsername())
                .build();
    }
}
