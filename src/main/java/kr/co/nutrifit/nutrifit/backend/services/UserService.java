package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.UserDto;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    //private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserDto userDto) throws Exception {
        if (userRepository.existsByUsername(userDto.getUsername()) || userRepository
                .existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email or Nickname is already used.");
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .oAuth(userDto.isOAuth())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(Role.ROLE_USER)
                .imageUrl(userDto.getProfileImage())
                .build();
        Point point = Point.builder()
                .points(0L)
                .build();
        Cart cart = Cart.builder()
                .cartItems(new ArrayList<>())
                .build();
        user.setPoint(point);
        user.setCart(cart);
        user.setCoupons(new ArrayList<>());
        user.setOrders(new ArrayList<>());
        return userRepository.save(user);
    }

    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 계정이 없습니다."));

        String tempPassword = generateTempPassword();

        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        //emailService.sendEmail(user.getEmail(), "[뉴트리핏] 비밀번호 재설정", "임시 비밀번호: "+ tempPassword);
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
