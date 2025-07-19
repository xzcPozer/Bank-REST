package com.sharf.tim.bank_rest.util;

import com.sharf.tim.bank_rest.dto.UserResponse;
import com.sharf.tim.bank_rest.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastname(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
