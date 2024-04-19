package com.faptic.cryptorecommendation.controller;

import com.faptic.cryptorecommendation.dto.Coin;
import com.faptic.cryptorecommendation.dto.CoinValues;
import com.faptic.cryptorecommendation.dto.DailyPrice;
import com.faptic.cryptorecommendation.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping(value = "/crypto")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * @param coin one of the enum coin values that we currently support
     *             its csv file is defined in /resources/prices in the format of {COIN}_values.csv
     *             and the enum value is defined in /dto/Coin.java
     * @return returns the oldest, newest, max and min prices
     */
    @GetMapping("/{coin}/info")
    public ResponseEntity<CoinValues> getInfo(@PathVariable("coin") Coin coin) {
        return ResponseEntity.ok(recommendationService.getInfo(coin));
    }

    /**
     * @return a descending sorted list of all the cryptos,
     * comparing the normalized range
     */
    @GetMapping("/normalized-range")
    public ResponseEntity<Map<Coin, Double>> getNormalizedRange() {
        return ResponseEntity.ok(recommendationService.getNormalizedRange());
    }

    /**
     * @param day the day we are querying for in the format yyyy-MM-dd
     * @return the coin with the highest normalized range for
     * that specific day
     */
    @PostMapping("/normalized-range/day")
    public ResponseEntity<DailyPrice> getNormalizedRangeByDay(@RequestBody LocalDate day) {
        return ResponseEntity.ok(recommendationService.getNormalizedRangeByDay(day));
    }
}
