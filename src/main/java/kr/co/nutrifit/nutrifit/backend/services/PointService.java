package kr.co.nutrifit.nutrifit.backend.services;

import kr.co.nutrifit.nutrifit.backend.dto.PointDto;
import kr.co.nutrifit.nutrifit.backend.dto.PointTransactionDto;
import kr.co.nutrifit.nutrifit.backend.persistence.PointRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.PointTransactionRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Point;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.PointTransaction;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.PointTransactionType;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointsRepository;
    private final PointTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addPoints(User user, long amount) {
        Point point = pointsRepository.findByUser(user);

        point.setPoints(point.getPoints() + amount);
        pointsRepository.save(point);

        // 트랜잭션 기록
        PointTransaction transaction = PointTransaction.builder()
                .point(point)
                .points(amount)
                .transactionType(PointTransactionType.REWARD)
                .description(LocalDateTime.now() + " 포인트 추가").build();
        transactionRepository.save(transaction);
    }

    @Transactional
    public void usePoints(User user, long amount) {
        Point point = pointsRepository.findByUser(user);

        if (point == null || point.getPoints() < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        point.setPoints(point.getPoints() - amount);
        pointsRepository.save(point);

        // 트랜잭션 기록
        PointTransaction transaction = PointTransaction.builder()
                .point(point)
                .points(-amount)
                .transactionType(PointTransactionType.USE)
                .description(LocalDateTime.now() + " 포인트 사용").build();;
        transactionRepository.save(transaction);
    }

    private PointTransactionDto convertToDto(PointTransaction transaction) {
        return PointTransactionDto.builder()
                .type(transaction.getTransactionType())
                .point(transaction.getPoints())
                .description(transaction.getDescription())
                .whenToBurn(transaction.getCreatedAt().plusMinutes(3L).toLocalDate())
                .build();
    }

    public PointDto getUserPoints(User user) {
        Point point = pointsRepository.findByUser(user);
        List<PointTransaction> transactions = transactionRepository.findByPoint(point);
        return PointDto.builder()
                .points(point.getPoints())
                .transactions(transactions.stream().map(this::convertToDto).toList())
                .build();
    }
}
