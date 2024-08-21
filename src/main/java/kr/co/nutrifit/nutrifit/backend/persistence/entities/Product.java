package kr.co.nutrifit.nutrifit.backend.persistence.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long originalPrice;

    private Long discountedPrice;

    @Column(nullable = false)
    private int stockQuantity;

    private int lowStockThreshold;

    @Column(columnDefinition = "text[]")
    private List<String> imageUrls;
    private String category;
    @Column(columnDefinition = "text[]")
    private List<String> badgeTexts;

    private Long reviewRating;
    private Long reviewCount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<Options> options;

    public void addOption(Options options) {
        if (this.options == null)
            this.options = new ArrayList<>();
        this.options.add(options);
        options.setProduct(this);
    }
}
