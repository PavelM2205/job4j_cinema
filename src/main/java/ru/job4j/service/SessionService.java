package ru.job4j.service;

import org.springframework.stereotype.Service;
import ru.job4j.dto.PlaceDTO;
import ru.job4j.model.Session;
import ru.job4j.repository.SessionsRepository;
import ru.job4j.repository.TicketRepository;

import java.util.*;

@Service
public class SessionService {
    private final SessionsRepository sessionStore;
    private final TicketRepository ticketStore;
    private static final int MAX_ROW = 7;
    private static final int MAX_CELL = 6;

    public SessionService(SessionsRepository sessionStore, TicketRepository ticketStore) {
        this.sessionStore = sessionStore;
        this.ticketStore = ticketStore;
    }

    public Session add(Session session) {
        return sessionStore.addSession(session);
    }

    public Optional<Session> findById(int id) {
        return sessionStore.findById(id);
    }

    public boolean update(Session session) {
        return sessionStore.updateSession(session);
    }

    public List<Session> findAll() {
        return sessionStore.findAll();
    }

    public List<PlaceDTO> getFreePlaces(int sessionId) {
        List<PlaceDTO> freePlaces = getAllPlaces();
        ticketStore.findAllTicketsForSomeSession(sessionId)
                .stream()
                .map(ticket -> new PlaceDTO(0, ticket.getRow(), ticket.getCell()))
                .forEach(placeDTO -> {
                    for (var el : freePlaces) {
                        if (el.equals(placeDTO)) {
                            el.setTaken(true);
                        }
                    }
                });
        return freePlaces;
    }

    public PlaceDTO findPlaceById(int id) {
        return getAllPlaces().get(id);
    }

    public List<PlaceDTO> getAllPlaces() {
        List<PlaceDTO> result = new ArrayList<>();
        int id = 0;
        for (int row = 1; row <= MAX_ROW; row++) {
            for (int cell = 1; cell <= MAX_CELL; cell++) {
                result.add(new PlaceDTO(id, row, cell));
                id++;
            }
        }
        return result;
    }
}
