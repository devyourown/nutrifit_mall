package kr.co.nutrifit.nutrifit.backend.dto;

import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private boolean isOAuth;
    private String password;
    private String profileImage;
    private String token;
    private Role role;
}
