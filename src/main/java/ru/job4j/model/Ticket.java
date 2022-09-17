package ru.job4j.model;

import java.util.Objects;

public class Ticket {
    private int id;
    private Session session;
    private User user;
    private Place place;

    public Ticket() {
    }

    public Ticket(int id, Place place) {
        this.id = id;
        this.place = place;
    }

    public Ticket(Session session, User user, Place place) {
        this.session = session;
        this.user = user;
        this.place = place;
    }

    public Ticket(int id, Session session, User user, Place place) {
        this.id = id;
        this.session = session;
        this.user = user;
        this.place = place;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) o;
        return id == ticket.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ticket{" + "id=" + id
                + ", session=" + session
                + ", user=" + user
                + ", place=" + place
                + '}';
    }
}
