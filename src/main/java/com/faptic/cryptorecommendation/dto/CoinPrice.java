package com.faptic.cryptorecommendation.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CoinPrice {
    private Instant date;
    private double price;
}
