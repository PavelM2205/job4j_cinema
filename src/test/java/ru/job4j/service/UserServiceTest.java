package ru.job4j.service;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.Main;
import ru.job4j.exception.UserByEmailAndPhoneNotFound;
import ru.job4j.exception.UserWithSuchEmailAndPhoneAlreadyExists;
import ru.job4j.model.User;
import ru.job4j.repository.UsersRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private static BasicDataSource pool;

    @BeforeAll
    public static void getPool() {
        pool = new Main().loadPool();
    }

    @Test
    public void whenAddUsersReturnsOptionalEmptyThenMustBeException() {
        UsersRepository userStore = mock(UsersRepository.class);
        UserService service = new UserService(userStore);
        User user = mock(User.class);
        when(userStore.addUser(user)).thenReturn(Optional.empty());
        assertThrows(UserWithSuchEmailAndPhoneAlreadyExists.class, () -> service.add(user));
    }

    @Test
    public void whenFindByIdReturnsOptionalEmptyThenMustBeException() {
        UsersRepository userStore = new UsersRepository(pool);
        UserService service = new UserService(userStore);
        assertThrows(IllegalArgumentException.class, () -> service.findById(0));
    }

    @Test
    public void whenFindUserByEmailAndPhoneReturnsOptionalEmptyThenMustBeException() {
        UsersRepository userStore = mock(UsersRepository.class);
        UserService service = new UserService(userStore);
        String email = "email";
        String phone = "phone";
        when(userStore.findUserByEmailAndPhone(email, phone))
                .thenReturn(Optional.empty());
        assertThrows(UserByEmailAndPhoneNotFound.class,
                () -> service.findUserByEmailAndPhone(email, phone));
    }
}