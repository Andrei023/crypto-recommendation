package com.faptic.cryptorecommendation.dto;

public enum Coin {
    BTC("BTC"),
    DOGE("DOGE"),
    ETH("ETH"),
    LTC("LTC"),
    XRP("XRP"),;

    private final String coinName;

    Coin(String coinName) {
        this.coinName = coinName;
    }

    public String getCoinName() {
        return this.coinName;
    }

    public static Coin fromString(String coinName) {
        for (Coin coin : Coin.values()) {
            if (coin.getCoinName().equalsIgnoreCase(coinName)) {
                return coin;
            }
        }
        throw new IllegalArgumentException("No coin with name " + coinName + " found");
    }
}
