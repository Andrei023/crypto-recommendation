package com.faptic.cryptorecommendation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoinValues {
    private double oldest;
    private double newest;
    private double max;
    private double min;
}
