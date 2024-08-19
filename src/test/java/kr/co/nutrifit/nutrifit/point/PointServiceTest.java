package kr.co.nutrifit.nutrifit.point;

import kr.co.nutrifit.nutrifit.backend.persistence.PointRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.PointTransactionRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.UserRepository;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Point;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.PointTransaction;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Role;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import kr.co.nutrifit.nutrifit.backend.services.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointTransactionRepository transactionRepository;

    @InjectMocks
    private PointService pointService;

    private User user;
    private Point point;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        point = Point.builder()
                .user(user)
                .points(100L)
                .build();
    }

    @Test
    void addPoints_ShouldIncreasePoints() {
        when(pointRepository.findByUser(user)).thenReturn(point);
        when(pointRepository.save(any(Point.class))).thenReturn(point);

        pointService.addPoints(user, 50L);

        verify(pointRepository, times(1)).save(point);
        verify(transactionRepository, times(1)).save(any(PointTransaction.class));
        assertEquals(150L, point.getPoints());
    }

    @Test
    void addPoints_ShouldCreateNewPointIfNotExist() {
        when(pointRepository.findByUser(user)).thenReturn(null);
        when(pointRepository.save(any(Point.class))).thenReturn(point);

        pointService.addPoints(user, 50L);

        verify(pointRepository, times(1)).save(any(Point.class));
        verify(transactionRepository, times(1)).save(any(PointTransaction.class));
    }

    @Test
    void usePoints_ShouldDecreasePoints() {
        when(pointRepository.findByUser(user)).thenReturn(point);
        when(pointRepository.save(any(Point.class))).thenReturn(point);

        pointService.usePoints(user, 50L);

        verify(pointRepository, times(1)).save(point);
        verify(transactionRepository, times(1)).save(any(PointTransaction.class));
        assertEquals(50L, point.getPoints());
    }

    @Test
    void usePoints_ShouldThrowExceptionIfInsufficientPoints() {
        when(pointRepository.findByUser(user)).thenReturn(point);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.usePoints(user, 150L);
        });

        String expectedMessage = "포인트가 부족합니다.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(pointRepository, times(0)).save(point);
        verify(transactionRepository, times(0)).save(any(PointTransaction.class));
    }

    @Test
    void usePoints_ShouldThrowExceptionIfNoPointsExist() {
        when(pointRepository.findByUser(user)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.usePoints(user, 50L);
        });

        String expectedMessage = "포인트가 부족합니다.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(pointRepository, times(0)).save(any(Point.class));
        verify(transactionRepository, times(0)).save(any(PointTransaction.class));
    }
}