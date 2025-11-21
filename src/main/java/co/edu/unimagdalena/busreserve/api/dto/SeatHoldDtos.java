package co.edu.unimagdalena.busreserve.api.dto;
import co.edu.unimagdalena.busreserve.domine.entities.HoldStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

public class SeatHoldDtos {

    public record SeatHoldCreateRequest(
            @NotNull Long tripId,
            @NotNull @Min(1) Integer seatNumber,
            @NotNull Long userId
            // expiresAt REMOVED (server-controlled)
    ) implements Serializable {}

    public record SeatHoldResponse(
            Long id,
            Long tripId,
            Integer seatNumber,
            Long userId,
            LocalDateTime expiresAt,
            HoldStatus status
    ) implements Serializable {}
}