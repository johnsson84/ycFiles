package johnsson84.ycFiles.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import johnsson84.ycFiles.dto.AuthRequest;
import johnsson84.ycFiles.dto.AuthResponse;
import johnsson84.ycFiles.dto.RegisterRequest;
import johnsson84.ycFiles.dto.RegisterResponse;
import johnsson84.ycFiles.models.Role;
import johnsson84.ycFiles.models.User;
import johnsson84.ycFiles.repositories.UserRepository;
import johnsson84.ycFiles.service.UserService;
import johnsson84.ycFiles.utils.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request,
                                   HttpServletResponse response) {

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

        try {
            // authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // set authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // get UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // generate JWT token
            String jwt = jwtUtil.generateToken(userDetails);

            // generate JWT cookie
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(false) // OBS! set to true in production with HTTPS
                    .path("/")
                    .maxAge(10 * 60 * 60)
                    .sameSite("Lax") // "Strict", "Lax", or "None"
                    .build();

            // add cookie to response
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            // return response without JWT in body
            AuthResponse authResponse = new AuthResponse(
                    "Login successful",
                    userDetails.getUsername(),
                    userService.findUserByEmail(userDetails.getUsername()).getRoles()
            );

            System.out.println("Logged in!");

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(authResponse);


        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            // Aauthentication failed
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password");
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {


        // check if the mail is already in use
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Mail is already taken");
        }

        // map the registration request to a User entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        // assign roles
        user.setRoles(Set.of(Role.USER));

        // register the user using UserService
        userService.register(user);

        // create a response object
        RegisterResponse regResponse = new RegisterResponse(
                "User registered successfully",
                user.getEmail(),
                user.getRoles()
        );

        return ResponseEntity.ok(regResponse);
    }

    @GetMapping("/check/{userToCheck}")
    public ResponseEntity<?> checkAuthentication(@PathVariable String userToCheck) {
        // Get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Return not authenticated
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // Retrive 'UserDetails' object from authentication context.
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();

        // Get user from DB
        User user = userService.findUserByEmail(userDetails.getUsername());

        // check username
        if (!userToCheck.equals(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // Return authenticated user
        return ResponseEntity.ok(new AuthResponse(
                "Authenticated",
                user.getEmail(),
                user.getRoles()
        ));
    }

    // Logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", null)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .httpOnly(true)
                .build();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logout successful");
    }
}