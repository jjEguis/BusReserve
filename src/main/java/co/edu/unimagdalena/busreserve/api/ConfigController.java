
package co.edu.unimagdalena.busreserve.api;

import co.edu.unimagdalena.busreserve.services.interfaces.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Validated
public class ConfigController {
    private final ConfigService service;

    @GetMapping("/{key}")
    public ResponseEntity<String> getValue(@PathVariable String key) {
        return ResponseEntity.ok(service.getValue(key));
    }

    @PutMapping("/{key}")
    public ResponseEntity<Void> setValue(@PathVariable String key,
                                         @RequestParam String value) {
        service.setValue(key, value);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hold-time")
    public ResponseEntity<Integer> getHoldTimeMinutes() {
        return ResponseEntity.ok(service.getHoldTimeMinutes());
    }

    @GetMapping("/overbooking-percentage")
    public ResponseEntity<Double> getOverbookingPercentage() {
        return ResponseEntity.ok(service.getOverbookingPercentage());
    }

    @GetMapping("/no-show-fee")
    public ResponseEntity<Double> getNoShowFee() {
        return ResponseEntity.ok(service.getNoShowFee());
    }

    @PutMapping("/hold-time")
    public ResponseEntity<Void> updateHoldTime(@RequestParam Integer minutes) {
        service.updateHoldTime(minutes);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/overbooking-percentage")
    public ResponseEntity<Void> updateOverbookingPercentage(@RequestParam Double percentage) {
        service.updateOverbookingPercentage(percentage);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/no-show-fee")
    public ResponseEntity<Void> updateNoShowFee(@RequestParam Double fee) {
        service.updateNoShowFee(fee);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllConfigs() {
        return ResponseEntity.ok(Map.of(
                "holdTimeMinutes", service.getHoldTimeMinutes(),
                "overbookingPercentage", service.getOverbookingPercentage(),
                "noShowFee", service.getNoShowFee()
        ));
    }
}