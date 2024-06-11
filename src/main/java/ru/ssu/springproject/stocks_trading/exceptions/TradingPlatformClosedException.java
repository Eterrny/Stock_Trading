package ru.ssu.springproject.stocks_trading.exceptions;

public class TradingPlatformClosedException extends RuntimeException {
    public TradingPlatformClosedException(String message) {
        super(message);
    }
}