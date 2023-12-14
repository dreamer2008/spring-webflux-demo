package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Mono<User> getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @GetMapping
    public Flux<User> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public Mono<Void> save(@RequestBody Mono<User> userMono) {
        return userService.save(userMono);
    }
}
