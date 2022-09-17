package ru.job4j.repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.Main;
import ru.job4j.model.Place;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class PlaceRepositoryTest {
    private static BasicDataSource pool;

    @BeforeAll
    public static void getPool() {
        pool = new Main().loadPool();
    }

    @Test
    public void whenFindAll() {
        PlaceRepository store = new PlaceRepository(pool);
        List<Place> placesFromDB = store.findAll();
        assertThat(placesFromDB.size()).isNotEqualTo(0);
    }

    @Test
    public void whenFindById() {
        PlaceRepository store = new PlaceRepository(pool);
        Place placeFromDB = store.findById(1).get();
        assertThat(placeFromDB.getId()).isEqualTo(1);
        assertThat(placeFromDB.getRow()).isNotEqualTo(0);
        assertThat(placeFromDB.getCell()).isNotEqualTo(0);
    }
}