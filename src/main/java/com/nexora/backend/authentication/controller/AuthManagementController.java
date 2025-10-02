package com.nexora.backend.authentication.controller;

import com.nexora.backend.authentication.service.AuthenticationService;
import com.nexora.backend.domain.request.AuthenticationRequest;
import com.nexora.backend.domain.request.RegistrationRequest;
import com.nexora.backend.domain.request.UpdateRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.domain.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthManagementController {

    @NonNull
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest registrationRequest) {
        log.info("RegistrationRequest: {}", registrationRequest);
        return ResponseEntity.ok(authenticationService.register(registrationRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        log.info("AuthenticationRequest: {}", request.toString());
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Refresh Request: {} Response: {}", request.toString(), response.toString());
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/get-all-users")
    public ResponseEntity<APIResponse> getAllUsers(@RequestParam Integer page, @RequestParam Integer size) {
        log.info("Get All Users");
        return authenticationService.getAllAuthentications(page, size);
    }

    @PostMapping("/get-user-by-email/{email}")
    public ResponseEntity<APIResponse> getUserByEmail(@PathVariable String email) throws IOException {
        log.info("Get User By Email: {}", email);
        return authenticationService.findEmployeeByEmail(email);
    }

    @GetMapping("/get-user-by-id/{id}")
    public ResponseEntity<APIResponse> getUserById(@PathVariable Integer id) {
        log.info("Get User By ID: {}", id);
        return authenticationService.findEmployeeById(id);
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<APIResponse> deleteUser(@PathVariable Integer id) {
        log.info("Delete User By ID: {}", id);
        return authenticationService.deleteUser(id);
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<APIResponse> updateUser(@PathVariable Integer id, @RequestBody UpdateRequest updateRequest){
        log.info("UpdateRequest for user ID {}: {}", id, updateRequest);
        return authenticationService.updateUser(id, updateRequest);
    }
}