package com.crackit.SpringSecurityJWT.auth;

import com.crackit.SpringSecurityJWT.Service.RefreshTokenService;
import com.crackit.SpringSecurityJWT.config.JwtService;
import com.crackit.SpringSecurityJWT.exception.CustomExceptions;
import com.crackit.SpringSecurityJWT.user.RefreshToken;
import com.crackit.SpringSecurityJWT.user.Role;
import com.crackit.SpringSecurityJWT.user.User;
import com.crackit.SpringSecurityJWT.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.naming.InvalidNameException;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private RefreshTokenService refreshTokenService;

    private AuthenticationRequest authenticationRequest;

    private  final UserRepository userRepository;
    private  final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private static final String EMAIL_PATTERN = "^[a-z0-9+_.-]+@[a-z0-9.-]+$";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{7,}$";
    public AuthenticationResponse register(RegisterRequest registerRequest) {

        validateEmail(registerRequest.getEmail());
        ValidateName(registerRequest.getFirstName());
        ValidateName(registerRequest.getLastName());
        validatePassword(registerRequest.getPassword());
        // Check if the email already exists
        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new CustomExceptions.EmailAlreadyExistsException("Email already exists change the email and try again");
        }

        // Create a new user
        User user = createUserFromRegisterRequest(registerRequest);

        // Save the user to the database
        User savedUser = userRepository.save(user);


        // Generate a JWT token
        String jwtToken = jwtService.generateToken(savedUser);

        // Return the authentication response with the JWT token
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }


    private User createUserFromRegisterRequest(RegisterRequest registerRequest) {
        return User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .build();
    }

    private void validateEmail(String email) {
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            throw new CustomExceptions.InvalidEmailException("Invalid email format correct the email and try again");
        }
    }

    private void ValidateName(String name) {
        if (name != null && name.startsWith(" ")) {
            throw new CustomExceptions.InvalidNameException("Name should not contain a space at the beginning");
        }
    }

    public void validatePassword(String password) {
        if (!Pattern.matches(PASSWORD_PATTERN, password)) {
            throw new CustomExceptions.InvalidPasswordException("Invalid password. Password must contain both characters and numbers and be at least 7 characters long.");
        }
    }








//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        //FirstStep
//            //We need to validate our request (validate whether password & username is correct)
//            //Verify whether user present in the database
//            //Which AuthenticationProvider -> DaoAuthenticationProvider (Inject)
//            //We need to authenticate using authenticationManager injecting this authenticationProvider
//        //SecondStep
//            //Verify whether userName and password is correct => UserNamePasswordAuthenticationToken
//            //Verify whether user present in db
//            //generateToken
//            //Return the token
    public AuthenticationResponse authenticate(AuthenticationRequest request) throws AuthenticationException {
        // First step: authenticate the user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Fetch the user from the database
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        Role role = user.getRole();

        // Generate a refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        // Build and return the authentication response
        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .reToken(refreshToken.getToken())
                .role(role) // Include the role in the response
                .build();

    }
}




