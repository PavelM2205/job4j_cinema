package ru.job4j.service;

import org.springframework.stereotype.Service;
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

    public Optional<User> add(User user) {
        return store.addUser(user);
    }

    public Optional<User> findById(int id) {
        return store.findById(id);
    }

    public boolean update(User user) {
        return store.updateUser(user);
    }

    public List<User> findAll() {
        return store.findAll();
    }

    public Optional<User> findUserByEmailAndPhone(String email, String phone) {
        return store.findUserByEmailAndPhone(email, phone);
    }
}
