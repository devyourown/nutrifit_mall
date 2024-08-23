package kr.co.nutrifit.nutrifit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaverUserDto {
    private String id;           // 네이버 사용자 ID
    private String nickname;     // 사용자 닉네임
    private String email;        // 사용자 이메일
    private String profile_image; // 프로필 이미지 URL
}

