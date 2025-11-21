package co.edu.unimagdalena.busreserve.api;
import co.edu.unimagdalena.busreserve.api.dto.AssignmentDtos.*;
import co.edu.unimagdalena.busreserve.services.interfaces.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Validated
public class AssignmentController {
    private final AssignmentService service;

    @PostMapping
    public ResponseEntity<AssignmentResponse> create(@Valid @RequestBody AssignmentCreateRequest req,
                                                     UriComponentsBuilder uriBuilder) {
        var body = service.create(req);
        var location = uriBuilder.path("/api/assignments/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping("/by-trip")
    public ResponseEntity<List<AssignmentResponse>> listByTrip(@RequestParam Long tripId) {
        return ResponseEntity.ok(service.listByTrip(tripId));
    }

    @GetMapping("/by-driver")
    public ResponseEntity<List<AssignmentResponse>> findByDriver(@RequestParam Long driverId) {
        return ResponseEntity.ok(service.findByDriver(driverId));
    }

    @GetMapping("/by-dispatcher")
    public ResponseEntity<List<AssignmentResponse>> findByDispatcher(@RequestParam Long dispatcherId) {
        return ResponseEntity.ok(service.findByDispatcher(dispatcherId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AssignmentResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody AssignmentUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PostMapping("/{id}/approve-checklist")
    public ResponseEntity<Void> approveChecklist(@PathVariable Long id) {
        service.approveChecklist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate-driver")
    public ResponseEntity<Void> validateDriverAvailability(@RequestParam Long driverId,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        service.validateDriverAvailability(driverId, start, end);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate-bus")
    public ResponseEntity<Void> validateBusAvailability(@RequestParam Long busId,
                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        service.validateBusAvailability(busId, start, end);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}