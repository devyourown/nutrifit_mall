package kr.co.nutrifit.nutrifit.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignDto {
    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "이메일은 유효해야 합니다.")
    private String email;

    @NotBlank(message = "닉네임을 채워 주세요.")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]+$", message = "닉네임은 한글, 숫자, 영문만 입력 가능합니다.")
    private String username;

    @NotBlank(message = "비밀번호를 채워 주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 8글자 이상으로 영문, 특수문자, 숫자가 포함되어야 합니다."
    )
    private String password;
}
