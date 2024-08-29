package kr.co.nutrifit.nutrifit.backend.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientPhone;

    @Column(nullable = false)
    private String ordererName;

    @Column(nullable = false)
    private String ordererPhone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String addressDetail;

    @Column
    private String cautions;

    @OneToMany(mappedBy = "shipping", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShippingStatus> statuses;

    public void addStatus(ShippingStatus status) {
        if (statuses == null)
            statuses = new ArrayList<>();
        statuses.add(status);
    }
}
