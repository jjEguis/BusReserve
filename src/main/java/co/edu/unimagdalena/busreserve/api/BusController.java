package co.edu.unimagdalena.busreserve.api;

import co.edu.unimagdalena.busreserve.api.dto.BusDtos.*;
import co.edu.unimagdalena.busreserve.services.interfaces.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
@Validated
public class BusController {

    private final BusService service;

    @PostMapping
    public ResponseEntity<BusResponse> create(@Valid @RequestBody BusCreateRequest req,
                                              UriComponentsBuilder uriBuilder) {
        var body = service.create(req);
        var location = uriBuilder.path("/api/buses/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping("/plate/{plate}")
    public ResponseEntity<BusResponse> getByPlate(@PathVariable String plate) {
        return ResponseEntity.ok(service.getByPlate(plate));
    }

    @GetMapping
    public ResponseEntity<Page<BusResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.list(PageRequest.of(page, size, Sort.by("plate").ascending())));
    }

    @GetMapping("/available")
    public ResponseEntity<List<BusResponse>> findAvailable() {
        return ResponseEntity.ok(service.findAvailable());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BusResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody BusUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Void> setAvailability(@PathVariable Long id,
                                                @RequestParam boolean available) {
        service.setAvailability(id, available);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
