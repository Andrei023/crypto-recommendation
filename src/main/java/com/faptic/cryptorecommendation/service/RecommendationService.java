package com.faptic.cryptorecommendation.service;

import com.faptic.cryptorecommendation.dto.Coin;
import com.faptic.cryptorecommendation.dto.CoinValues;
import com.faptic.cryptorecommendation.dto.DailyPrice;

import java.time.LocalDate;
import java.util.Map;

public interface RecommendationService {

    CoinValues getInfo(Coin coin);

    Map<Coin, Double> getNormalizedRange();

    DailyPrice getNormalizedRangeByDay(LocalDate day);
}
