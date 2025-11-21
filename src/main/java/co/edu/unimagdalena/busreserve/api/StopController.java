package co.edu.unimagdalena.busreserve.api;

import co.edu.unimagdalena.busreserve.api.dto.StopDtos.*;
import co.edu.unimagdalena.busreserve.services.interfaces.StopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/stops")
@RequiredArgsConstructor
@Validated
public class StopController {

    private final StopService service;

    @PostMapping
    public ResponseEntity<StopResponse> create(@Valid @RequestBody StopCreateRequest req,
                                               UriComponentsBuilder uriBuilder) {
        var body = service.create(req);
        var location = uriBuilder.path("/api/stops/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StopResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<StopResponse>> listByRoute(@RequestParam Long routeId) {
        return ResponseEntity.ok(service.listByRoute(routeId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StopResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody StopUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
