package com.movieflix.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.movieflix.controller.request.UserRequest;
import com.movieflix.controller.response.UserResponse;
import com.movieflix.entity.User;

class UserMapperTest {

    @Test
    void toUser_shouldMapAllFieldsFromRequest() {
        UserRequest request = new UserRequest(null, "Lucas", "lucas@teste.com", "123456");

        User user = UserMapper.toUser(request);

        assertThat(user.getName()).isEqualTo("Lucas");
        assertThat(user.getEmail()).isEqualTo("lucas@teste.com");
        assertThat(user.getPassword()).isEqualTo("123456");
    }

    @Test
    void toUserResponse_shouldNotExposePassword() {
        User user = User.builder().id(1L).name("Lucas").email("lucas@teste.com").password("hash").build();

        UserResponse response = UserMapper.toUserResponse(user);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Lucas");
        assertThat(response.email()).isEqualTo("lucas@teste.com");
    }
}
