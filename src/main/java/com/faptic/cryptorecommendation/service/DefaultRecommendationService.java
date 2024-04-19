package com.faptic.cryptorecommendation.service;

import com.faptic.cryptorecommendation.dto.Coin;
import com.faptic.cryptorecommendation.dto.CoinPrice;
import com.faptic.cryptorecommendation.dto.CoinValues;
import com.faptic.cryptorecommendation.dto.DailyPrice;
import com.faptic.cryptorecommendation.repository.RecommendationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DefaultRecommendationService implements RecommendationService {

    private final RecommendationRepository repository;

    public DefaultRecommendationService(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public CoinValues getInfo(Coin coin) {
        try {
            List<CoinPrice> allPricesByCoin = repository.getPrices(coin);

            CoinPrice oldestPrice = Collections.min(allPricesByCoin, Comparator.comparing(CoinPrice::getDate));
            CoinPrice newestPrice = Collections.max(allPricesByCoin, Comparator.comparing(CoinPrice::getDate));
            CoinPrice minPrice = Collections.min(allPricesByCoin, Comparator.comparing(CoinPrice::getPrice));
            CoinPrice maxPrice = Collections.max(allPricesByCoin, Comparator.comparing(CoinPrice::getPrice));

            return CoinValues.builder()
                    .oldest(oldestPrice.getPrice())
                    .newest(newestPrice.getPrice())
                    .min(minPrice.getPrice())
                    .max(maxPrice.getPrice())
                    .build();

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<Coin, Double> getNormalizedRange() {
        Map<Coin, Double> normalizedRangeMap = new HashMap<>();
        Map<Coin, List<CoinPrice>> allPriceByCoin = repository.getAllPricesByCoin();
        for (Map.Entry<Coin, List<CoinPrice>> entry : allPriceByCoin.entrySet()) {
            CoinPrice minPrice = Collections.min(entry.getValue(), Comparator.comparing(CoinPrice::getPrice));
            CoinPrice maxPrice = Collections.max(entry.getValue(), Comparator.comparing(CoinPrice::getPrice));
            double normalizedRange = (maxPrice.getPrice() - minPrice.getPrice()) / minPrice.getPrice();
            normalizedRangeMap.put(entry.getKey(), normalizedRange);
        }
        Map<Coin, Double> sortedNormalizedRangeMap = new LinkedHashMap<>();
        normalizedRangeMap.entrySet()
                .stream()
                .sorted(Map.Entry.<Coin, Double>comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedNormalizedRangeMap.put(x.getKey(), x.getValue()));
        return sortedNormalizedRangeMap;
    }

    @Override
    public DailyPrice getNormalizedRangeByDay(LocalDate day) {
        Map<Coin, List<Double>> dailyCoinPrices = new HashMap<>();
        Map<Coin, List<CoinPrice>> coinPrices = repository.getAllPricesByCoin();
        for (Map.Entry<Coin, List<CoinPrice>> entry : coinPrices.entrySet()) {
            for (CoinPrice coinPrice : entry.getValue()) {
                LocalDate currentDay = LocalDate.ofInstant(coinPrice.getDate(), ZoneId.of("UTC"));
                if (!currentDay.equals(day)) {
                    continue;
                }
                if (dailyCoinPrices.containsKey(entry.getKey())) {
                    dailyCoinPrices.get(entry.getKey()).add(coinPrice.getPrice());
                } else {
                    List<Double> prices = new ArrayList<>();
                    prices.add(coinPrice.getPrice());
                    dailyCoinPrices.put(entry.getKey(), prices);
                }
            }
        }
        Map<Coin, Double> dailyNormalizedRangePrices = new HashMap<>();
        for (Map.Entry<Coin, List<Double>> entry : dailyCoinPrices.entrySet()) {
            Coin coin = entry.getKey();
            double minPrice = Collections.min(entry.getValue());
            double maxPrice = Collections.max(entry.getValue());
            dailyNormalizedRangePrices.put(coin, (maxPrice - minPrice) / minPrice);
        }
        if (dailyNormalizedRangePrices.isEmpty()) {
            return null;
        }
        return DailyPrice.builder()
                .price(
                        Collections.max(dailyNormalizedRangePrices.entrySet(), Map.Entry.comparingByValue()).getValue())
                .coin(Collections.max(dailyNormalizedRangePrices.entrySet(), Map.Entry.comparingByValue()).getKey())
                .build();
    }
}
