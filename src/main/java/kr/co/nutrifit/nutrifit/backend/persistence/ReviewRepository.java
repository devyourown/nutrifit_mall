package kr.co.nutrifit.nutrifit.backend.persistence;

import kr.co.nutrifit.nutrifit.backend.persistence.entities.Product;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.Review;
import kr.co.nutrifit.nutrifit.backend.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct(Product product);
    List<Review> findByUser(User user);
}
