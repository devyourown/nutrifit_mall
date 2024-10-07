package kr.co.nutrifit.nutrifit.backend.dto;

import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    public UserDto(Long id, String username, String email, String profileImage, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
    }
    private Long id;
    private String username;
    private String email;
    private boolean isOAuth;
    private String password;
    private String profileImage;
    private String token;
    private Role role;
    private LocalDateTime createdAt;
}
