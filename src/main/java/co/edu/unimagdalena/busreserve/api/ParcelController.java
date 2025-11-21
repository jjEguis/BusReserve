package co.edu.unimagdalena.busreserve.api;
import co.edu.unimagdalena.busreserve.api.dto.ParcelDtos.*;
import co.edu.unimagdalena.busreserve.domine.entities.ParcelStatus;
import co.edu.unimagdalena.busreserve.services.interfaces.ParcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;
@RestController
@RequestMapping("/api/parcels")
@RequiredArgsConstructor
@Validated
public class ParcelController {
    private final ParcelService service;

    @PostMapping
    public ResponseEntity<ParcelResponse> create(@Valid @RequestBody ParcelCreateRequest req,
                                                 UriComponentsBuilder uriBuilder) {
        var body = service.create(req);
        var location = uriBuilder.path("/api/parcels/{id}").buildAndExpand(body.id()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParcelResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ParcelResponse> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    @GetMapping("/from-stop")
    public ResponseEntity<List<ParcelResponse>> findByFromStop(@RequestParam Long fromStopId) {
        return ResponseEntity.ok(service.findByFromStop(fromStopId));
    }

    @GetMapping("/to-stop")
    public ResponseEntity<List<ParcelResponse>> findByToStop(@RequestParam Long toStopId) {
        return ResponseEntity.ok(service.findByToStop(toStopId));
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<ParcelResponse>> findByStatus(@RequestParam ParcelStatus status) {
        return ResponseEntity.ok(service.findByStatus(status));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ParcelResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody ParcelUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @RequestParam ParcelStatus status) {
        service.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deliver")
    public ResponseEntity<Void> deliverParcel(@RequestParam String code,
                                              @RequestParam String otp,
                                              @RequestParam String proofPhotoUrl) {
        service.deliverParcel(code, otp, proofPhotoUrl);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
