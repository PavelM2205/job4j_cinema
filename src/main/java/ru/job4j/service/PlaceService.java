package ru.job4j.service;

import org.springframework.stereotype.Service;
import ru.job4j.model.Place;
import ru.job4j.repository.PlaceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<Place> findAll() {
        return placeRepository.findAll();
    }

    public Place findById(int id) {
        Optional<Place> optPlace = placeRepository.findById(id);
        if (optPlace.isEmpty()) {
            throw new IllegalArgumentException("Place with such id does not exist");
        }
        return optPlace.get();
    }
}
