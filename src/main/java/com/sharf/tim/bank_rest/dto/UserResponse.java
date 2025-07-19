package com.sharf.tim.bank_rest.dto;

import lombok.Builder;

@Builder
public record UserResponse (
        Long id,
        String firstName,
        String lastname,
        String email
){
}
