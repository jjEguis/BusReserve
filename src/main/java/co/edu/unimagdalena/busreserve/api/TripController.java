package co.edu.unimagdalena.busreserve.api;

import co.edu.unimagdalena.busreserve.api.dto.TripDtos.*;
import co.edu.unimagdalena.busreserve.domine.entities.TripStatus;
import co.edu.unimagdalena.busreserve.services.interfaces.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Validated
public class TripController {

    private final TripService service;

    @PostMapping
    public ResponseEntity<TripResponse> create(@Valid @RequestBody TripCreateRequest req,
                                               UriComponentsBuilder uriBuilder) {
        var body = service.create(req);
        var location = uriBuilder.path("/api/trips/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<TripResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.list(PageRequest.of(page, size, Sort.by("departureAt").ascending())));
    }

    @GetMapping("/available")
    public ResponseEntity<List<TripResponse>> findAvailable(@RequestParam String origin,
                                                            @RequestParam String destination,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(service.findAvailableTrips(origin, destination, date));
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<TripResponse>> findByStatus(@RequestParam TripStatus status) {
        return ResponseEntity.ok(service.findByStatus(status));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TripResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody TripUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @RequestParam TripStatus status) {
        service.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/boarding/open")
    public ResponseEntity<Void> openBoarding(@PathVariable Long id) {
        service.openBoarding(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/boarding/close")
    public ResponseEntity<Void> closeBoarding(@PathVariable Long id) {
        service.closeBoarding(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/depart")
    public ResponseEntity<Void> depart(@PathVariable Long id) {
        service.depart(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
