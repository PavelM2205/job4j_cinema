package ru.job4j.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.dto.PlaceDTO;
import ru.job4j.model.Session;
import ru.job4j.model.Ticket;
import ru.job4j.model.User;
import ru.job4j.service.SessionService;
import ru.job4j.service.TicketService;
import ru.job4j.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TicketController {
    private final SessionService sessionService;
    private final UserService userService;
    private final TicketService ticketService;

    public TicketController(SessionService sessionService, UserService userService,
                            TicketService ticketService) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @PostMapping("/getTicket")
    public String getTicket(@RequestParam(name = "sess_id", required = false, defaultValue = "0") int sessionId,
                             HttpSession httpSession, Model model,
                             @RequestParam(value = "place", required = false) List<Integer> placesId) {
        if (httpSession.getAttribute("not_specify_place").equals(true)) {
            model.addAttribute("not_specify_place", true);
            httpSession.setAttribute("not_specify_place", false);
            return "ticket_result";
        }
        User user = userService.findById(((User) httpSession.getAttribute("user")).getId()).get();
        Session session = sessionService.findById(sessionId).get();
        List<Ticket> tickets = new ArrayList<>();
        for (var el : placesId) {
            PlaceDTO placeDTO = sessionService.findPlaceById(el);
            tickets.add(new Ticket(
                    session,
                    user,
                    placeDTO.getRow(),
                    placeDTO.getCell()
            ));
        }
        model.addAttribute("fail_get_ticket", false);
        for (var ticket : tickets) {
            Optional<Ticket> optTicket = ticketService.add(ticket);
            if (optTicket.isEmpty()) {
                model.addAttribute("failTicket", ticket);
                model.addAttribute("fail_get_ticket", true);
            }
        }
        return "ticket_result";
    }
}
