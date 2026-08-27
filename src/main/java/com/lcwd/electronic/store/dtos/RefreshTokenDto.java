package com.lcwd.electronic.store.dtos;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenDto {

    private Long id;
    private String token;
    private Instant expiryDate;
    private UserDto user;
}
