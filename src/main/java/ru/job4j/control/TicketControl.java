package ru.job4j.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.models.Place;
import ru.job4j.models.Session;
import ru.job4j.models.Ticket;
import ru.job4j.models.User;
import ru.job4j.service.SessionService;
import ru.job4j.service.TicketService;
import ru.job4j.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TicketControl {
    private final SessionService sessionService;
    private final UserService userService;
    private final TicketService ticketService;

    public TicketControl(SessionService sessionService, UserService userService,
                         TicketService ticketService) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @PostMapping("/getTicket")
    public String getTicket(@RequestParam(name = "sess_id") int sessionId,
                             HttpSession httpSession, Model model,
                             @RequestParam("place") List<Integer> placesId) {
        User user = userService.findById(((User) httpSession.getAttribute("user")).getId()).get();
        Session session = sessionService.findById(sessionId).get();
        List<Ticket> tickets = new ArrayList<>();
        for (var el : placesId) {
            Place place = sessionService.findPlaceById(el);
            tickets.add(new Ticket(
                    session,
                    user,
                    place.getRow(),
                    place.getCell()
            ));
        }
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
