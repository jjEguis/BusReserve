package co.edu.unimagdalena.busreserve.api;

import co.edu.unimagdalena.busreserve.api.dto.FareRuleDtos.*;
import co.edu.unimagdalena.busreserve.services.interfaces.FareRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/fare-rules")
@RequiredArgsConstructor
@Validated
public class FareRuleController {

    private final FareRuleService service;

    @PostMapping
    public ResponseEntity<FareRuleResponse> create(@Valid @RequestBody FareRuleCreateRequest req,
                                                   UriComponentsBuilder uriBuilder) {
        var body = service.create(req);
        var location = uriBuilder.path("/api/fare-rules/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FareRuleResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<FareRuleResponse>> listByRoute(@RequestParam Long routeId) {
        return ResponseEntity.ok(service.listByRoute(routeId));
    }

    @GetMapping("/segment")
    public ResponseEntity<FareRuleResponse> findByRouteAndStops(@RequestParam Long routeId,
                                                                @RequestParam Long fromStopId,
                                                                @RequestParam Long toStopId) {
        return ResponseEntity.ok(service.findByRouteAndStops(routeId, fromStopId, toStopId));
    }

    @GetMapping("/calculate")
    public ResponseEntity<Double> calculatePrice(@RequestParam Long routeId,
                                                 @RequestParam Long fromStopId,
                                                 @RequestParam Long toStopId) {
        return ResponseEntity.ok(service.calculatePrice(routeId, fromStopId, toStopId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FareRuleResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody FareRuleUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
