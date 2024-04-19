package com.faptic.cryptorecommendation.service;

import com.faptic.cryptorecommendation.dto.Coin;
import com.faptic.cryptorecommendation.dto.CoinPrice;
import com.faptic.cryptorecommendation.dto.CoinValues;
import com.faptic.cryptorecommendation.dto.DailyPrice;
import com.faptic.cryptorecommendation.repository.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.faptic.cryptorecommendation.dto.Coin.BTC;
import static com.faptic.cryptorecommendation.dto.Coin.ETH;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultRecommendationServiceTest {

    @Mock
    RecommendationRepository repository;

    RecommendationService service;

    @BeforeEach
    void setUp() {
        service = new DefaultRecommendationService(repository);
    }

    @Test
    void shouldReturnInfoWhenMultiplePriceEntries() {
        when(repository.getPrices(BTC)).thenReturn(List.of(
                CoinPrice.builder().date(Instant.ofEpochMilli(1641009600000L)).price(46813.21D).build(),
                CoinPrice.builder().date(Instant.ofEpochMilli(1641880800000L)).price(42110D).build(),
                CoinPrice.builder().date(Instant.ofEpochMilli(1642176000000L)).price(43109D).build(),
                CoinPrice.builder().date(Instant.ofEpochMilli(1643486400000L)).price(37853D).build(),
                CoinPrice.builder().date(Instant.ofEpochMilli(1643220000000L)).price(38040.9D).build()
        ));
        CoinValues result = service.getInfo(BTC);
        assertEquals(46813.21D, result.getOldest());
        assertEquals(37853D, result.getNewest());
        assertEquals(46813.21D, result.getMax());
        assertEquals(37853D, result.getMin());
    }

    @Test
    void shouldReturnInfoWhenSinglePriceEntry() {
        when(repository.getPrices(BTC)).thenReturn(List.of(
                CoinPrice.builder().date(Instant.ofEpochMilli(1641009600000L)).price(46813.21D).build()
        ));
        CoinValues result = service.getInfo(BTC);
        assertEquals(46813.21D, result.getOldest());
        assertEquals(46813.21D, result.getNewest());
        assertEquals(46813.21D, result.getMax());
        assertEquals(46813.21D, result.getMin());
    }

    @Test
    void shouldReturnNullWhenNoPriceEntries() {
        when(repository.getPrices(BTC)).thenReturn(emptyList());
        CoinValues result = service.getInfo(BTC);
        assertNull(result);
    }

    @Test
    void shouldReturnValidDataWhenGetNormalizedRange() {
        when(repository.getAllPricesByCoin()).thenReturn(Map.of(
                BTC, List.of(
                        CoinPrice.builder().date(Instant.ofEpochMilli(1641009600000L)).price(46813.21D).build(),
                        CoinPrice.builder().date(Instant.ofEpochMilli(1642176000000L)).price(43109D).build(),
                        CoinPrice.builder().date(Instant.ofEpochMilli(1643486400000L)).price(37853D).build()),
                ETH, List.of(
                        CoinPrice.builder().date(Instant.ofEpochMilli(1641088800000L)).price(3747D).build(),
                        CoinPrice.builder().date(Instant.ofEpochMilli(1642075200000L)).price(3334D).build())
        ));
        Map<Coin, Double> result = service.getNormalizedRange();
        assertEquals(2, result.keySet().size());
        assertEquals(0.23671069664227404, result.get(BTC));
        assertEquals(0.123875224955009, result.get(ETH));
    }

    @Test
    void shouldReturnValidDataWhenGetNormalizedRangeWithSinglePriceEntry() {
        when(repository.getAllPricesByCoin()).thenReturn(Map.of(
                BTC, List.of(
                        CoinPrice.builder().date(Instant.ofEpochMilli(1641009600000L)).price(46813.21D).build()
                )));
        Map<Coin, Double> result = service.getNormalizedRange();
        assertEquals(1, result.keySet().size());
        assertEquals(0, result.get(BTC));
    }

    @Test
    void shouldReturnEmptyWhenGetNormalizedRangeWithNoData() {
        when(repository.getAllPricesByCoin()).thenReturn(new HashMap<>());
        Map<Coin, Double> result = service.getNormalizedRange();
        assertEquals(0, result.keySet().size());
    }

    @Test
    void shouldReturnValidDataWhenGetNormalizedRangeByDay() {
        when(repository.getAllPricesByCoin()).thenReturn(Map.of(
                BTC, List.of(
                        // 2022-01-01
                        CoinPrice.builder().date(Instant.ofEpochMilli(1641009600000L)).price(46813.21D).build(),
                        // 2022-01-01
                        CoinPrice.builder().date(Instant.ofEpochMilli(1641020400000L)).price(46979.61D).build(),
                        // 2022-01-01
                        CoinPrice.builder().date(Instant.ofEpochMilli(1641031200000L)).price(47143.98D).build(),
                        // 2022-01-11
                        CoinPrice.builder().date(Instant.ofEpochMilli(1641880800000L)).price(42110D).build()),
                ETH, List.of(
                        //2022-01-01
                        CoinPrice.builder().date(Instant.ofEpochMilli(1641049200000L)).price(3697.04D).build(),
                        //2022-01-13
                        CoinPrice.builder().date(Instant.ofEpochMilli(1642075200000L)).price(3334D).build())
        ));
        DailyPrice result = service.getNormalizedRangeByDay(LocalDate.parse("2022-01-01"));
        assertEquals(BTC, result.getCoin());
        assertEquals(0.007065740631757662, result.getPrice());
    }
}
