package co.edu.unimagdalena.busreserve.api.dto;

import co.edu.unimagdalena.busreserve.domine.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserDtos {

    public record UserCreateRequest(
            @NotBlank String name,
            @Email @NotBlank String email,
            @NotBlank String phone,
            @NotBlank String password
    ) implements Serializable {}

    public record UserUpdateRequest(
            String name,
            String email,
            String phone
    ) implements Serializable {}

    public record UserResponse(
            Long id,
            String name,
            String email,
            String phone,
            Role role,
            Boolean status,
            LocalDateTime createdAt
    ) implements Serializable {}

    public record LoginRequest(
            @NotBlank String email,
            @NotBlank String password
    ) implements Serializable {}




}