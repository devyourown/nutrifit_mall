package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.SignDto;
import kr.co.nutrifit.nutrifit.backend.lib.EmailService;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(SignDto signDto) throws Exception {
        if (userRepository.existsByUsername(signDto.getUsername()) || userRepository
                .existsByEmail(signDto.getEmail())) {
            throw new IllegalArgumentException("Email or Nickname is already used.");
        }

        User user = User.builder()
                .username(signDto.getUsername())
                .email(signDto.getEmail())
                .password(passwordEncoder.encode(signDto.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        return userRepository.save(user);
    }

    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 계정이 없습니다."));

        String tempPassword = generateTempPassword();

        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        emailService.sendEmail(user.getEmail(), "[뉴트리핏] 비밀번호 재설정", "임시 비밀번호: "+ tempPassword);
    }

    private String generateTempPassword() {
        int length = 8;
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder tempPassword = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * charSet.length());
            tempPassword.append(charSet.charAt(randomIndex));
        }

        return tempPassword.toString();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
