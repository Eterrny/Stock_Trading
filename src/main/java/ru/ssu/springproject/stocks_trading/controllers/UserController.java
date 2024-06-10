package ru.ssu.springproject.stocks_trading.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ssu.springproject.stocks_trading.models.User;
import ru.ssu.springproject.stocks_trading.services.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
//
//    @GetMapping("/login")
//    public String showLoginPage() {
//        return "login";
//    }
}


