package ru.job4j.exception;

public class UserByEmailAndPhoneNotFound extends RuntimeException {
    public UserByEmailAndPhoneNotFound(String message) {
        super(message);
    }
}
