package kr.co.nutrifit.nutrifit.backend.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orderer")
public class Orderer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String recipientName;

    @NotNull
    private String recipientPhone;

    @NotNull
    private String ordererName;

    @NotNull
    private String ordererPhone;

    @NotNull
    private String address;

    @NotNull
    private String addressDetail;

    private String cautions;
}
