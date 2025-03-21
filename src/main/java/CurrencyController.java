import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {
    private List<Currency> currencies = new ArrayList<>();

    @GetMapping
    public List<Currency> getCurrencies() {
        return currencies;
    }

    @PostMapping
    public void addCurrency(@RequestBody CurrencyRequest currencyRequest) {
        Currency currency = new Currency();
        currency.setId(String.valueOf(currencies.size() + 1));
        currency.setName(currencyRequest.getName());
        currency.setBaseCurrency(currencyRequest.getBaseCurrency());
        currency.setPriceChangeRange(currencyRequest.getPriceChangeRange());
        currency.setDescription(currencyRequest.getDescription());
        currencies.add(currency);
    }

    @GetMapping("/{id}")
    public Currency getCurrency(@PathVariable String id) {
        return currencies.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Currency not found"));
    }

    @PutMapping("/{id}")
    public void updateCurrency(@PathVariable String id, @RequestBody CurrencyRequest currencyRequest) {
        Currency currency = getCurrency(id);
        currency.setName(currencyRequest.getName());
        currency.setBaseCurrency(currencyRequest.getBaseCurrency());
        currency.setPriceChangeRange(currencyRequest.getPriceChangeRange());
        currency.setDescription(currencyRequest.getDescription());
    }

    @DeleteMapping("/{id}")
    public void deleteCurrency(@PathVariable String id) {
        currencies.removeIf(c -> c.getId().equals(id));
    }
}
