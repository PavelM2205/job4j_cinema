package ru.job4j.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.job4j.exception.TicketWithSuchSessionAndPlaceAlreadyExists;
import ru.job4j.exception.UserByEmailAndPhoneNotFound;
import ru.job4j.exception.UserWithSuchEmailAndPhoneAlreadyExists;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(UserWithSuchEmailAndPhoneAlreadyExists.class)
    public ModelAndView userWithSuchEmailAndPhoneAlreadyExists() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("registration");
        mv.addObject("fail", true);
        mv.addObject("message",
                "Пользователь с такой почтой и/или номером телефона уже существует");
        return mv;
    }

    @ExceptionHandler(UserByEmailAndPhoneNotFound.class)
    public ModelAndView userNotFound() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("login");
        mv.addObject("fail", true);
        mv.addObject("message",
                "Неверно введены электронная почта и/или номер телефона");
        return mv;
    }

    @ExceptionHandler(TicketWithSuchSessionAndPlaceAlreadyExists.class)
    public ModelAndView ticketWithSuchSessionAndPlaceAlreadyExists(
            TicketWithSuchSessionAndPlaceAlreadyExists exc) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("ticketResult");
        mv.addObject("failTicket", exc.getTicket());
        mv.addObject("fail_get_ticket", true);
        return mv;
    }
}
