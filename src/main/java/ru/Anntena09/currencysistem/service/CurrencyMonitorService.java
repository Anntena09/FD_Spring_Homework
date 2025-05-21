package ru.Anntena09.currencysistem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.Anntena09.currencysistem.dto.CbrCurrencyResponse;
import ru.Anntena09.currencysistem.model.Currency;
import ru.Anntena09.currencysistem.repository.CurrencyRepository;
import java.util.List;

@Service
public class CurrencyMonitorService {
    private static final Logger log = LoggerFactory.getLogger(CurrencyMonitorService.class);

    private final RestTemplate restTemplate;
    private final CurrencyRepository currencyRepository;

    private static final String CBR_API_URL = "https://www.cbr-xml-daily.ru/daily_json.js";

    public CurrencyMonitorService(CurrencyRepository currencyRepository) {
        this.restTemplate = new RestTemplate();
        this.currencyRepository = currencyRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkCurrencyRates() {
        log.info("Starting currency rates check...");

        try {
            CbrCurrencyResponse response = restTemplate.getForObject(CBR_API_URL, CbrCurrencyResponse.class);

            if (response == null || response.getValute() == null) {
                log.warn("Empty response from CBR API");
                return;
            }

            List<Currency> trackedCurrencies = currencyRepository.findAll();

            if (trackedCurrencies.isEmpty()) {
                log.info("No currencies tracked in database");
                return;
            }

            for (Currency tracked : trackedCurrencies) {
                CbrCurrencyResponse.CurrencyRate rate = response.getValute().get(tracked.getBaseCurrency());

                if (rate != null) {
                    checkCurrencyChange(tracked, rate);
                } else {
                    log.warn("Currency {} ({}) not found in CBR response",
                            tracked.getName(), tracked.getBaseCurrency());
                }
            }

        } catch (Exception e) {
            log.error("Error during currency rates check", e);
        }
    }

    private void checkCurrencyChange(Currency tracked, CbrCurrencyResponse.CurrencyRate rate) {
        double changePercent = ((rate.getValue() - rate.getPrevious()) / rate.getPrevious()) * 100;
        String range = tracked.getPriceChangeRange();

        try {
            double threshold = Double.parseDouble(range.replace("%", "").trim());

            if (changePercent >= Math.abs(threshold) && threshold > 0) {
                log.info("{} increased by {:.2f}% (threshold: {}%) - {}",
                        tracked.getName(), changePercent, threshold, tracked.getDescription());
            } else if (changePercent <= -Math.abs(threshold) && threshold < 0) {
                log.info("{} decreased by {:.2f}% (threshold: {}%) - {}",
                        tracked.getName(), Math.abs(changePercent), Math.abs(threshold), tracked.getDescription());
            }
        } catch (NumberFormatException e) {
            log.error("Invalid priceChangeRange format '{}' for currency {}",
                    range, tracked.getName());
        }
    }
}