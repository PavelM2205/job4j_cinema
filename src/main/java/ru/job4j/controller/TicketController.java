package ru.job4j.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.model.Place;
import ru.job4j.model.Session;
import ru.job4j.model.Ticket;
import ru.job4j.model.User;
import ru.job4j.service.PlaceService;
import ru.job4j.service.SessionService;
import ru.job4j.service.TicketService;
import ru.job4j.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class TicketController {
    private final SessionService sessionService;
    private final UserService userService;
    private final TicketService ticketService;
    private final PlaceService placeService;

    public TicketController(SessionService sessionService, UserService userService,
                            TicketService ticketService, PlaceService placeService) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.ticketService = ticketService;
        this.placeService = placeService;
    }

    @PostMapping("/getTicket")
    public String getTicket(@RequestParam(name = "sess_id", required = false, defaultValue = "0") int sessionId,
                             HttpSession httpSession, Model model,
                             @RequestParam(value = "place", required = false) List<Integer> placesId) {
        if (Objects.equals(true, httpSession.getAttribute("not_specify_place"))) {
            model.addAttribute("not_specify_place", true);
            httpSession.setAttribute("not_specify_place", false);
            return "ticketResult";
        }
        User user = userService.findById(((User) httpSession.getAttribute("user")).getId());
        Session session = sessionService.findById(sessionId);
        List<Ticket> tickets = new ArrayList<>();
        for (var el : placesId) {
            Place place = placeService.findById(el);
            tickets.add(new Ticket(
                    session,
                    user,
                    place
            ));
        }
        model.addAttribute("fail_get_ticket", false);
        for (var ticket : tickets) {
            ticketService.add(ticket);
        }
        return "ticketResult";
    }
}
