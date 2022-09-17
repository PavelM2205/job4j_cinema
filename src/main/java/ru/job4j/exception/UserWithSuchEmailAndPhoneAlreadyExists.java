package ru.job4j.exception;

public class UserWithSuchEmailAndPhoneAlreadyExists extends RuntimeException {
    public UserWithSuchEmailAndPhoneAlreadyExists(String message) {
        super(message);
    }
}
