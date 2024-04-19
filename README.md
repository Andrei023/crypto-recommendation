Crypto recommendation

This is a Spring Boot application.
To start the app, run mvn clean install, then start CryptoRecommendationApplication.

The app reads the price data from /resources/prices folder (csv files with timestamp, coin name, price) and exposes severeal endpoints:
- getInfo: returns the oldest, newest, max and min prices for a specific coin
- getNormalizedRange: returns a descending sorted list of all the coins comparing the normalized range
- getNormalizedRangeByDay: returns the coin with the highest normalized range for that specific day

To add a new coin
- add a new csv file in /resources/prices with the name pattern {COIN}_values.csv
- specify the new coin name in Coin.java enum
