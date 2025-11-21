package co.edu.unimagdalena.busreserve.api;
import co.edu.unimagdalena.busreserve.api.dto.IncidentDtos.*;
import co.edu.unimagdalena.busreserve.domine.entities.EntityType;
import co.edu.unimagdalena.busreserve.domine.entities.IncidentType;
import co.edu.unimagdalena.busreserve.services.interfaces.IncidentService;
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
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@Validated
public class IncidentController {
    private final IncidentService service;

    @PostMapping
    public ResponseEntity<IncidentResponse> create(@Valid @RequestBody IncidentCreateRequest req,
                                                   UriComponentsBuilder uriBuilder) {
        var body = service.create(req);
        var location = uriBuilder.path("/api/incidents/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<IncidentResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.list(PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/by-entity")
    public ResponseEntity<List<IncidentResponse>> findByEntityTypeAndId(@RequestParam EntityType entityType,
                                                                        @RequestParam Long entityId) {
        return ResponseEntity.ok(service.findByEntityTypeAndId(entityType, entityId));
    }

    @GetMapping("/by-type")
    public ResponseEntity<Page<IncidentResponse>> findByType(@RequestParam IncidentType type,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.findByType(type, PageRequest.of(page, size)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}