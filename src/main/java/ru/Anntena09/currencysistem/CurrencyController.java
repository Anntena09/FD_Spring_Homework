package ru.Anntena09.currencysistem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.Anntena09.currencysistem.model.Currency;
import ru.Anntena09.currencysistem.repository.CurrencyRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {
    private final CurrencyRepository currencyRepository;

    public CurrencyController(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @GetMapping
    public ResponseEntity<List<Currency>> getCurrencies() {
        return ResponseEntity.ok(currencyRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Currency> addCurrency(@RequestBody CurrencyRequest currencyRequest) {
        Currency currency = new Currency();
        currency.setName(currencyRequest.getName());
        currency.setBaseCurrency(currencyRequest.getBaseCurrency());
        currency.setPriceChangeRange(currencyRequest.getPriceChangeRange());
        currency.setDescription(currencyRequest.getDescription());
        Currency savedCurrency = currencyRepository.save(currency);
        return ResponseEntity.status(201).body(savedCurrency);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Currency> getCurrency(@PathVariable UUID id) {
        return currencyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Currency> getCurrency(@PathVariable UUID id, @RequestBody CurrencyRequest currencyRequest) {
        return currencyRepository.findById(id)
                .map(currency -> {
                    currency.setName(currencyRequest.getName());
                    currency.setBaseCurrency(currencyRequest.getBaseCurrency());
                    currency.setPriceChangeRange(currencyRequest.getPriceChangeRange());
                    currency.setDescription(currencyRequest.getDescription());
                    return ResponseEntity.ok(currencyRepository.save(currency));
                })
                .orElseThrow(() -> new RuntimeException("ru.Anntena09.currencysistem.model.Currency not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable UUID id) {
        if (currencyRepository.existsById(id)) {
            currencyRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
