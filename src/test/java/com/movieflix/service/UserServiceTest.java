package com.movieflix.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.movieflix.entity.User;
import com.movieflix.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void saveUser_shouldEncodePasswordBeforeSaving() {
        User user = User.builder().name("Lucas").email("lucas@teste.com").password("123456").build();
        when(passwordEncoder.encode("123456")).thenReturn("hash-gerado");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.saveUser(user);

        assertThat(result.getPassword()).isEqualTo("hash-gerado");
    }
}
