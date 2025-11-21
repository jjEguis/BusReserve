package co.edu.unimagdalena.busreserve.api;

import co.edu.unimagdalena.busreserve.api.dto.TicketDtos.*;
import co.edu.unimagdalena.busreserve.domine.entities.TicketStatus;
import co.edu.unimagdalena.busreserve.services.interfaces.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Validated
public class TicketController {
    private final TicketService service;

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketCreateRequest req,
                                                 UriComponentsBuilder uriBuilder) {
        var body = service.create(req);
        var location = uriBuilder.path("/api/tickets/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<TicketResponse> getByQrCode(@PathVariable String qrCode) {
        return ResponseEntity.ok(service.getByQrCode(qrCode));
    }

    @GetMapping("/by-trip")
    public ResponseEntity<List<TicketResponse>> findByTrip(@RequestParam Long tripId) {
        return ResponseEntity.ok(service.findByTrip(tripId));
    }

    @GetMapping("/by-passenger")
    public ResponseEntity<List<TicketResponse>> findByPassenger(@RequestParam Long passengerId) {
        return ResponseEntity.ok(service.findByPassenger(passengerId));
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<TicketResponse>> findByStatus(@RequestParam TicketStatus status) {
        return ResponseEntity.ok(service.findByStatus(status));
    }

    @GetMapping("/count-sold")
    public ResponseEntity<Long> countSoldSeatsByTrip(@RequestParam Long tripId) {
        return ResponseEntity.ok(service.countSoldSeatsByTrip(tripId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TicketResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody TicketUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/no-show")
    public ResponseEntity<Void> markAsNoShow(@PathVariable Long id) {
        service.markAsNoShow(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validateTicket(@RequestParam String qrCode) {
        service.validateTicket(qrCode);
        return ResponseEntity.noContent().build();
    }
}