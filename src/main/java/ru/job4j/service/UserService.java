package ru.job4j.service;

import org.springframework.stereotype.Service;
import ru.job4j.exception.UserByEmailAndPhoneNotFound;
import ru.job4j.exception.UserWithSuchEmailAndPhoneAlreadyExists;
import ru.job4j.model.User;
import ru.job4j.repository.UsersRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UsersRepository store;

    public UserService(UsersRepository store) {
        this.store = store;
    }

    public User add(User user) {
        Optional<User> optUser = store.addUser(user);
        if (optUser.isEmpty()) {
            throw new UserWithSuchEmailAndPhoneAlreadyExists("User was not added");
        }
        return optUser.get();
    }

    public User findById(int id) {
        Optional<User> optUser = store.findById(id);
        if (optUser.isEmpty()) {
            throw new IllegalArgumentException("User with such id does not exists");
        }
        return optUser.get();
    }

    public boolean update(User user) {
        return store.updateUser(user);
    }

    public List<User> findAll() {
        return store.findAll();
    }

    public User findUserByEmailAndPhone(String email, String phone) {
        Optional<User> optUser = store.findUserByEmailAndPhone(email, phone);
        if (optUser.isEmpty()) {
            throw new UserByEmailAndPhoneNotFound(
                    "User with such email and phone does nit exist");
        }
        return optUser.get();
    }
}
