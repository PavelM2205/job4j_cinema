package ru.job4j.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.models.User;
import ru.job4j.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class UserControl {
    private final UserService userService;

    public UserControl(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registrationForm")
    public String registrationForm() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute User user, Model model) {
        Optional<User> optUser = userService.add(user);
        if (optUser.isEmpty()) {
            model.addAttribute("fail", true);
            model.addAttribute("message",
                    "Пользователь с такой почтой и/или номером телефона уже существует");
            return "registration";
        }
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
        Optional<User> userFromDB = userService.findUserByEmailAndPhone(user.getEmail(),
                user.getPhone());
        if (userFromDB.isEmpty()) {
            model.addAttribute("fail", true);
            model.addAttribute("message",
                    "Неверно введены электронная почта и/или номер телефона");
            return "login";
        }
        httpSession.setAttribute("user", userFromDB.get());
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/loginPage";
    }
}
