package com.movieflix.service;

import org.springframework.stereotype.Service;

import com.movieflix.entity.User;
import com.movieflix.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

}
