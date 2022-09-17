package ru.job4j.service;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.Main;
import ru.job4j.repository.PlaceRepository;

import static org.junit.jupiter.api.Assertions.*;

class PlaceServiceTest {
    private static BasicDataSource pool;

    @BeforeAll
    public static void getPool() {
        pool = new Main().loadPool();
    }

    @Test
    public void whenWrongIdThenFindByIdTrowsException() {
        PlaceRepository store = new PlaceRepository(pool);
        PlaceService service = new PlaceService(store);
        assertThrows(IllegalArgumentException.class, () -> service.findById(0));
    }
}