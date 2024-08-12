package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.persistence.PointRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.PointTransactionRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Point;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.PointTransaction;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.PointTransactionType;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointsRepository;
    private final PointTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addPoints(String username, long amount, String description) {
        User user = userRepository.findByUsername(username);
        Point point = pointsRepository.findByUser(user);

        // 포인트가 없으면 새로 생성
        if (point == null) {
            point = Point.builder().user(user).points(0L).build();
        }

        point.setPoints(point.getPoints() + amount);
        pointsRepository.save(point);

        // 트랜잭션 기록
        PointTransaction transaction = PointTransaction.builder()
                .user(user)
                .points(amount)
                .transactionType(PointTransactionType.REWARD)
                .description(description).build();
        transactionRepository.save(transaction);
    }

    @Transactional
    public void usePoints(String username, long amount, String description) {
        User user = userRepository.findByUsername(username);
        Point point = pointsRepository.findByUser(user);

        if (point == null || point.getPoints() < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        point.setPoints(point.getPoints() - amount);
        pointsRepository.save(point);

        // 트랜잭션 기록
        PointTransaction transaction = PointTransaction.builder()
                .user(user)
                .points(-amount)
                .transactionType(PointTransactionType.USE)
                .description(description).build();;
        transactionRepository.save(transaction);
    }
}
