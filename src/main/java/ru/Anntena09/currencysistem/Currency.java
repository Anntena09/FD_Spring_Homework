package ru.Anntena09.currencysistem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Currency {
    private String id;
    private String name;
    private String baseCurrency;
    private String priceChangeRange;
    private String description;
}

