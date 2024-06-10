package ru.ssu.springproject.stocks_trading.exceptions;

public class NotEnoughQuantityException extends RuntimeException {
    public NotEnoughQuantityException(String message) {
        super(message);
    }
}