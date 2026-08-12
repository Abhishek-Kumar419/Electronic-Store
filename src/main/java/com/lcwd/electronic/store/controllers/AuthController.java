package com.lcwd.electronic.store.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.lcwd.electronic.store.dtos.JwtRequest;
import com.lcwd.electronic.store.dtos.JwtResponse;
import com.lcwd.electronic.store.dtos.UserDto;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.exceptions.BadApiRequestException;
import com.lcwd.electronic.store.security.JwtHelper;
import com.lcwd.electronic.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.OutputKeys;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("/auth")
//@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper helper;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${googleClientId}")
    private String googleClientId;
    @Value("${newPassword}")
    private String newPassword;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request){
        this.doAuthenticate(request.getEmail(), request.getPassword());
        //now token generation
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = helper.generateToken(userDetails);

        UserDto userDto = mapper.map(userDetails, UserDto.class);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .user(userDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);

    }



    private void doAuthenticate(String email, String password){

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email,password);
        try{
            manager.authenticate(authentication);
        }catch (BadCredentialsException e){
            throw new BadApiRequestException(" Invalid Username or Password !!");
        }
    }

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal){
        String name = principal.getName();
        return new ResponseEntity<>(mapper.map(userDetailsService.loadUserByUsername(name), UserDto.class), HttpStatus.OK);
    }



    //login with google api

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, Object> data) {

        // Get the ID token sent by the frontend
        if (!data.containsKey("idToken") || data.get("idToken") == null) {
            return ResponseEntity.badRequest().body("Missing 'idToken' in request body");
        }
        String token = data.get("idToken").toString();

        try {
            NetHttpTransport netHttpTransport = new NetHttpTransport();
            GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(netHttpTransport, JSON_FACTORY)
                            .setAudience(Collections.singleton(googleClientId))
                            .build();

            // Parse AND verify the Google ID token
            GoogleIdToken googleIdToken = verifier.verify(token);

            if (googleIdToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("Invalid Google ID token");
            }

            // Token is valid
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            logger.info("Payload : {}",payload);
            String email = payload.getEmail();

            User user=null;

            user=userService.findUserByEmailOptional(email).orElse(null);

            if (user==null) {
                //create new user
                String name = data.get("name") != null ? data.get("name").toString() : email;
                String photoUrl = data.get("photoUrl") != null ? data.get("photoUrl").toString() : "";
                user=this.saveUser(email, name, photoUrl);
            }

            ResponseEntity<JwtResponse> jwtResponseResponseEntity=this.login(JwtRequest.builder().email(user.getEmail()).password(newPassword).build());
            return jwtResponseResponseEntity;

//            String googleUserId = payload.getSubject();
//            return ResponseEntity.ok("Login successful");

        } catch (Exception e) {
            logger.error("Google auth error: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Google authentication failed: " + e.getMessage());
        }
    }

    private User saveUser(String email, String name, String photoUrl) {

        UserDto newUser = UserDto.builder()
                .name(name)
                .email(email)
                .password(newPassword)
                .imageName(photoUrl)
                .roles(new HashSet<>())
                .build();

        UserDto user = userService.createUser(newUser);

        return this.mapper.map(user, User.class);
    }

}
