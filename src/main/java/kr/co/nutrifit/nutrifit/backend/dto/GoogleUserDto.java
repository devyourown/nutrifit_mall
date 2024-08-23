package kr.co.nutrifit.nutrifit.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleUserDto {
    private String id;             // Google의 사용자 ID
    private String email;          // 사용자 이메일
    private boolean verified_email; // 이메일 인증 여부
    private String name;           // 사용자 이름
    private String given_name;     // 이름
    private String family_name;    // 성
    private String picture;        // 프로필 사진 URL
    private String locale;         // 사용자의 지역 설정
}
