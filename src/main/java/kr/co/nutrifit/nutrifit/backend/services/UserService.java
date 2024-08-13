package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.SignDto;
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
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(SignDto signDto) {
        if (userRepository.existsByUsername(signDto.getUsername()) || userRepository
                .existsByEmail(signDto.getEmail())) {
            throw new IllegalArgumentException("이메일 혹은 닉네임이 이미 사용중입니다.");
        }

        User user = User.builder()
                .username(signDto.getUsername())
                .email(signDto.getEmail())
                .password(passwordEncoder.encode(signDto.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        return userRepository.save(user);
    }
}
