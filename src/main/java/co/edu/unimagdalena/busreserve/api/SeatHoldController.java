package co.edu.unimagdalena.busreserve.api;

import co.edu.unimagdalena.busreserve.api.dto.SeatHoldDtos.*;
import co.edu.unimagdalena.busreserve.services.interfaces.SeatHoldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/seat-holds")
@RequiredArgsConstructor
@Validated
public class SeatHoldController {

    private final SeatHoldService service;

    @PostMapping
    public ResponseEntity<SeatHoldResponse> holdSeat(@Valid @RequestBody SeatHoldCreateRequest req,
                                                     UriComponentsBuilder uriBuilder) {
        var body = service.holdSeat(req);
        var location = uriBuilder.path("/api/seat-holds/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatHoldResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<SeatHoldResponse>> listByTrip(@RequestParam Long tripId) {
        return ResponseEntity.ok(service.listByTrip(tripId));
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> isSeatAvailable(@RequestParam Long tripId,
                                                   @RequestParam String seatNumber) {
        return ResponseEntity.ok(service.isSeatAvailable(tripId, seatNumber));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> releaseHold(@PathVariable Long id) {
        service.releaseHold(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/expire")
    public ResponseEntity<Void> expireHolds() {
        service.expireHolds();
        return ResponseEntity.noContent().build();
    }
}
