package com.lcwd.electronic.store.dtos;

import com.lcwd.electronic.store.entities.RefreshToken;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class JwtResponse {

    private String jwtToken;
    private UserDto user;
    private RefreshTokenDto refreshToken;
}
