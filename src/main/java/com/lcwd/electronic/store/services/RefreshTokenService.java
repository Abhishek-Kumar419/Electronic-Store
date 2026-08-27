package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.RefreshTokenDto;
import com.lcwd.electronic.store.dtos.UserDto;

public interface RefreshTokenService {

    RefreshTokenDto createRefreshToken(String username);

    RefreshTokenDto findByToken(String token);

    RefreshTokenDto verifyRefreshToken(RefreshTokenDto token);

    UserDto getUser(RefreshTokenDto refreshTokenDto);
}
