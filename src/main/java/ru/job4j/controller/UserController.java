package ru.job4j.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.model.User;
import ru.job4j.service.UserService;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registrationForm")
    public String registrationForm() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute User user, Model model) {
        userService.add(user);
        return "login";
    }

    @GetMapping("/loginPage")
    public String loginPage(Model model, HttpSession httpSession) {
        if (httpSession.getAttribute("mustLoginForTakeTicket") != null) {
            model.addAttribute("fail", true);
            model.addAttribute("message",
                    "Для приобретения билета необходимо войти в свою учетную запись");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, Model model, HttpSession httpSession) {
        User userFromDB = userService.findUserByEmailAndPhone(user.getEmail(),
                user.getPhone());
        httpSession.setAttribute("user", userFromDB);
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/loginPage";
    }
}
