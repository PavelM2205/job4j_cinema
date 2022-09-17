package ru.job4j.repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.model.Place;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PlaceRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PlaceRepository.class);
    private static final String SELECT_ALL = "SELECT * FROM places";
    private static final String SELECT_BY_ID = "SELECT * FROM places WHERE id = ?";
    private final BasicDataSource pool;

    public PlaceRepository(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Place> findAll() {
        List<Place> result = new ArrayList<>();
        try (PreparedStatement st = pool.getConnection().prepareStatement(SELECT_ALL)) {
            try (ResultSet res = st.executeQuery()) {
                while (res.next()) {
                    result.add(getPlaceFromResultSet(res));
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception when findAll from places DB: ", exc);
        }
        return result;
    }

    public Optional<Place> findById(int id) {
        Optional<Place> result = Optional.empty();
        try (PreparedStatement st = pool.getConnection().prepareStatement(SELECT_BY_ID)) {
            st.setInt(1, id);
            try (ResultSet res = st.executeQuery()) {
                if (res.next()) {
                    Place place = getPlaceFromResultSet(res);
                    result = Optional.of(place);
                }
            }
        } catch (Exception exc) {
            LOG.error("Exception when findById Place from DB: ", exc);
        }
        return result;
    }

    private Place getPlaceFromResultSet(ResultSet res) throws SQLException {
        return new Place(
                res.getInt("id"),
                res.getInt("pos_row"),
                res.getInt("cell")
        );
    }
}
