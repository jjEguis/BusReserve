package co.edu.unimagdalena.busreserve.api;

import co.edu.unimagdalena.busreserve.api.dto.BaggageDtos.*;
import co.edu.unimagdalena.busreserve.services.interfaces.BaggageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/baggage")
@RequiredArgsConstructor
@Validated
public class BaggageController {

    private final BaggageService service;

    @PostMapping
    public ResponseEntity<BaggageResponse> create(@Valid @RequestBody BaggageCreateRequest req,
                                                  @RequestParam Long ticketId,
                                                  UriComponentsBuilder uriBuilder) {
        var body = service.create(req, ticketId);
        var location = uriBuilder.path("/api/baggage/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaggageResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping("/tag/{tagCode}")
    public ResponseEntity<BaggageResponse> getByTagCode(@PathVariable String tagCode) {
        return ResponseEntity.ok(service.getByTagCode(tagCode));
    }

    @GetMapping
    public ResponseEntity<List<BaggageResponse>> listByTicket(@RequestParam Long ticketId) {
        return ResponseEntity.ok(service.listByTicket(ticketId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
