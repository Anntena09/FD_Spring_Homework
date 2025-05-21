package ru.Anntena09.currencysistem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "currencies")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "base_currency", nullable = false)
    private String baseCurrency;

    @Column(name = "price_change_range", nullable = false)
    private String priceChangeRange;

    @Column(columnDefinition = "TEXT")
    private String description;
}
