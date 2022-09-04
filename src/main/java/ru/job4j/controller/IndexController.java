package ru.job4j.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.model.User;
import ru.job4j.service.SessionService;

import javax.servlet.http.HttpSession;


@Controller
public class IndexController {
    private final SessionService sessionService;

    public IndexController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/index")
    public String index(Model model, HttpSession httpSession) {
        model.addAttribute("sessions", sessionService.findAll());
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setUsername("Гость");
        }
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/indexWithPlaces/{filmId}")
    public String indexWithPlaces(@PathVariable("filmId") int id,
                                  RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("sess_id", id);
        redirectAttributes.addFlashAttribute("freePlaces", sessionService.getFreePlaces(id));
        return "redirect:/index";
    }
}
