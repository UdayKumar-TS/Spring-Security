package com.crackit.SpringSecurityJWT.auth;

import com.crackit.SpringSecurityJWT.Service.RefreshTokenService;
import com.crackit.SpringSecurityJWT.config.JwtService;
import com.crackit.SpringSecurityJWT.exception.CustomExceptions;
import com.crackit.SpringSecurityJWT.exception.ErrorResponse;
import com.crackit.SpringSecurityJWT.user.RefreshToken;
import com.crackit.SpringSecurityJWT.user.RefreshTokenRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/crackit/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;


    private  final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

            AuthenticationResponse authResponse = authService.register(registerRequest);
            return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse authenticationResponse = authService.authenticate(request);
            if (authenticationResponse != null) {
                return ResponseEntity.ok(authenticationResponse);
            } else {
                throw new BadCredentialsException("Invalid credentials from backend ");
            }
        } catch (BadCredentialsException e) {
            throw e; // This will be caught by the ControllerAdvice
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }



    @PostMapping("/refreshToken")
    public AuthenticationResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequestDTO){
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo);
                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .reToken(refreshTokenRequestDTO.getToken()).build();
                }).orElseThrow(() ->new RuntimeException("Refresh Token is not in DB..!!"));
    }

}
