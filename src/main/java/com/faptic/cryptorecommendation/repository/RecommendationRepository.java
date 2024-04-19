package com.faptic.cryptorecommendation.repository;

import com.faptic.cryptorecommendation.dto.Coin;
import com.faptic.cryptorecommendation.dto.CoinPrice;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.faptic.cryptorecommendation.utils.FileUtils.readAllFiles;
import static com.faptic.cryptorecommendation.dto.Coin.fromString;

@Component
public class RecommendationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationRepository.class);

    private static Map<Coin, List<CoinPrice>> coinPricesMap;
    private static final String FOLDER_NAME = "prices";
    private static final String FILE_NAME_SUFFIX = "_values";

    @PostConstruct
    public void init() {
        coinPricesMap = new HashMap<>();
        try {
            Map<String, List<String[]>> coinPrices = readAllFiles(FOLDER_NAME, FILE_NAME_SUFFIX);
            for (Map.Entry<String, List<String[]>> entry : coinPrices.entrySet()) {
                Coin coin = fromString(entry.getKey());
                List<CoinPrice> coinPriceList = new ArrayList<>();
                for (String[] coinValues : entry.getValue()) {
                    coinPriceList.add(
                            CoinPrice.builder()
                                    .date(Instant.ofEpochMilli(Long.parseLong(coinValues[0])))
                                    .price(Double.parseDouble(coinValues[2]))
                                    .build());
                }
                coinPricesMap.put(coin, coinPriceList);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load coin prices");
        }
    }
    
    public Map<Coin, List<CoinPrice>> getAllPricesByCoin() {
        return coinPricesMap;
    }
    
    public List<CoinPrice> getPrices(Coin coin) {
        return coinPricesMap.get(coin);
    }
}
