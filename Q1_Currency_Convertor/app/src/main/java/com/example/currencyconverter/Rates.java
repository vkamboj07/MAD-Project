package com.example.currencyconverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;


public final class Rates {
    private static final Map<Currency, BigDecimal> USD_TO = new EnumMap<>(Currency.class);

    static {

        USD_TO.put(Currency.USD, bd("1.0"));
        USD_TO.put(Currency.INR, bd("94.00"));
        USD_TO.put(Currency.EUR, bd("0.92"));
        USD_TO.put(Currency.JPY, bd("150.00"));
    }

    private Rates() {}

    public static BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (from == to) return amount;
        BigDecimal usd = toUsd(amount, from);
        return fromUsd(usd, to);
    }

    private static BigDecimal toUsd(BigDecimal amount, Currency from) {
        BigDecimal usdToFrom = USD_TO.get(from);
        if (usdToFrom == null) throw new IllegalStateException("Missing rate for " + from);
        // amount (FROM) -> USD: amount / (USD->FROM)
        return amount.divide(usdToFrom, 12, RoundingMode.HALF_UP);
    }

    private static BigDecimal fromUsd(BigDecimal usd, Currency to) {
        BigDecimal usdToTo = USD_TO.get(to);
        if (usdToTo == null) throw new IllegalStateException("Missing rate for " + to);
        return usd.multiply(usdToTo);
    }

    private static BigDecimal bd(String v) {
        return new BigDecimal(v);
    }
}

