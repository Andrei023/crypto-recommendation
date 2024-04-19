package com.faptic.cryptorecommendation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DailyPrice {
    private Coin coin;
    private double price;
}
