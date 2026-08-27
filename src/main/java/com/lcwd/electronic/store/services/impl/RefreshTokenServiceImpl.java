package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.RefreshTokenDto;
import com.lcwd.electronic.store.dtos.UserDto;
import com.lcwd.electronic.store.entities.RefreshToken;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.repositories.RefreshTokenRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.RefreshTokenService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

   private UserRepository userRepository;

   private RefreshTokenRepository refreshTokenRepository;

   private ModelMapper modelMapper;

    public RefreshTokenServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RefreshTokenDto createRefreshToken(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(()-> new ResourceNotFoundException("user not found !!"));

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(null);

        if (refreshToken == null) {
            refreshToken = RefreshToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusSeconds(5 * 24 * 60 * 60))
                    .build();
        }else {
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusSeconds(5 * 24 * 60 * 60));
        }

        refreshTokenRepository.save(refreshToken);
        return modelMapper.map(refreshToken, RefreshTokenDto.class);
    }

    @Override
    public RefreshTokenDto findByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(()-> new ResourceNotFoundException("token not found in database!!"));
        return this.modelMapper.map(refreshToken, RefreshTokenDto.class);
    }

    @Override
    public RefreshTokenDto verifyRefreshToken(RefreshTokenDto token) {

        var refreshToken = modelMapper.map(token, RefreshToken.class);

        if (token.getExpiryDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh Token Expired !!");
        }
        //agar expired nhi h toh
        return token;

    }

    @Override
    public UserDto getUser(RefreshTokenDto dto) {
        RefreshToken refreshToken=refreshTokenRepository.findByToken(dto.getToken()).orElseThrow(()->new ResourceNotFoundException("token not found !!"));
        User user = refreshToken.getUser();
        return modelMapper.map(user, UserDto.class);
    }
}
