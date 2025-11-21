package co.edu.unimagdalena.busreserve.security.dto;

import co.edu.unimagdalena.busreserve.domine.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class AuthDtos {
    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            Set<Role> roles
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record AuthResponse(
            String accessToken,
            String tokenType,
            long expiresInSeconds
    ) {}
}