package co.edu.unimagdalena.busreserve.api;

import co.edu.unimagdalena.busreserve.api.dto.SeatDtos.*;
import co.edu.unimagdalena.busreserve.services.interfaces.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
@Validated
public class SeatController {

    private final SeatService service;

    @PostMapping
    public ResponseEntity<SeatResponse> create(@Valid @RequestBody SeatCreateRequest req,
                                               UriComponentsBuilder uriBuilder) {
        var body = service.create(req);
        var location = uriBuilder.path("/api/seats/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @PostMapping("/bulk")
    public ResponseEntity<Void> createSeatsForBus(@RequestParam Long busId,
                                                  @RequestParam int totalSeats) {
        service.createSeatsForBus(busId, totalSeats);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<SeatResponse>> listByBus(@RequestParam Long busId) {
        return ResponseEntity.ok(service.listByBus(busId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
