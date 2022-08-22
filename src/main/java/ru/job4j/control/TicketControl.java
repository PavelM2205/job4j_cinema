package ru.job4j.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.models.Session;
import ru.job4j.models.Ticket;
import ru.job4j.models.User;
import ru.job4j.persistence.TicketDBStore;
import ru.job4j.service.SessionService;
import ru.job4j.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class TicketControl {
    private final SessionService sessionService;
    private final UserService userService;
    private final TicketDBStore ticketStore;

    public TicketControl(SessionService sessionService, UserService userService,
                         TicketDBStore ticketStore) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.ticketStore = ticketStore;
    }

    @PostMapping("/getTicket")
    public String takeTicket(@ModelAttribute Ticket ticket,
                             @RequestParam(name = "session_id") int sessionId,
                             HttpSession httpSession, Model model) {
        Session session = sessionService.findById(sessionId).get();
        ticket.setSession(session);
        User user = userService.findById((int) httpSession.getAttribute("user_id")).get();
        ticket.setUser(user);
        Optional<Ticket> optionalTicket = ticketStore.add(ticket);
        if (optionalTicket.isEmpty()) {
            model.addAttribute("fail_get_ticket", true);
        }
        return "ticket_result";
    }
}
