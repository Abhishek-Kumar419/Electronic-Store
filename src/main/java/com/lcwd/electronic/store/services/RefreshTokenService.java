package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.RefreshTokenDto;
import com.lcwd.electronic.store.dtos.UserDto;
import com.lcwd.electronic.store.entities.RefreshToken;

public interface RefreshTokenService {


    //create
    RefreshTokenDto createRefreshToken(String username);

    //find by token
    RefreshTokenDto findByToken(String token);

    //verify
    RefreshTokenDto verifyRefreshToken(RefreshTokenDto refreshTokenDto);

    UserDto getUser(RefreshTokenDto dto);


}
