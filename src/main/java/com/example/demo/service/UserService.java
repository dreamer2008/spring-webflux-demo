package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private Map<Integer, User> map = new HashMap<>();

    public UserService() {
        map.put(1, new User("John"));
        map.put(2, new User("Mary"));
        map.put(3, new User("Alex"));
    }

    public Mono<User> getById(Integer id) {
        return Mono.justOrEmpty(map.get(id));
    }

    public Flux<User> getAll() {
        return Flux.fromIterable(map.values());
    }

    public Mono<Void> save(Mono<User> userMono) {
        return userMono.doOnNext(user -> {
            int id = map.size() + 1;
            map.put(id, user);
        }).thenEmpty(Mono.empty());
    }
}
