package alchemy.api;

import alchemy.logic.HoldfastManagerService;
import alchemy.object.Holdfast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/holdfast")
@CrossOrigin(origins = "*")
public class HoldfastController {

    @Autowired
    private HoldfastManagerService holdfastManagerService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllHoldfasts() {
        try {
            List<Holdfast> holdfasts = holdfastManagerService.getAllHoldfasts();
            return ResponseEntity.ok(holdfasts);
        } catch (Exception e) {
            System.err.println("Error in getAllHoldfasts: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving holdfasts: " + e.getMessage());
        }
    }

    @GetMapping("/{groupName}")
    public ResponseEntity<?> getHoldfastStatus(@PathVariable String groupName) {
        try {
            Map<String, Object> status = holdfastManagerService.getStatus(groupName);
            if (status == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            System.err.println("Error in getHoldfastStatus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving status: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createHoldfast(@RequestBody Map<String, String> payload) {
        try {
            String groupName = payload.get("groupName");
            String holdfastName = payload.getOrDefault("holdfastName", "Lockwood");
            if (groupName == null || groupName.isBlank()) {
                return ResponseEntity.badRequest().body("groupName is required");
            }
            // Check for duplicate
            if (holdfastManagerService.getHoldfast(groupName) != null) {
                return ResponseEntity.badRequest().body("A holdfast for group '" + groupName + "' already exists");
            }
            Holdfast h = holdfastManagerService.createHoldfast(groupName, holdfastName);
            return ResponseEntity.ok(h);
        } catch (Exception e) {
            System.err.println("Error in createHoldfast: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating holdfast: " + e.getMessage());
        }
    }

    @PostMapping("/advance")
    public ResponseEntity<?> advanceTime(@RequestBody Map<String, Object> payload) {
        try {
            String groupName = (String) payload.get("groupName");
            int days = ((Number) payload.get("days")).intValue();
            if (days <= 0) {
                return ResponseEntity.badRequest().body("days must be greater than 0");
            }
            if (days > 365) {
                return ResponseEntity.badRequest().body("Cannot advance more than 365 days at once");
            }
            Map<String, Object> result = holdfastManagerService.advanceTime(groupName, days);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error in advanceTime: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error advancing time: " + e.getMessage());
        }
    }

    @PostMapping("/build")
    public ResponseEntity<?> build(@RequestBody Map<String, String> payload) {
        try {
            String groupName = payload.get("groupName");
            String buildingType = payload.get("buildingType");
            if (groupName == null || buildingType == null) {
                return ResponseEntity.badRequest().body("groupName and buildingType are required");
            }
            Map<String, Object> result = holdfastManagerService.build(groupName, buildingType);
            boolean success = (boolean) result.get("success");
            if (!success) {
                return ResponseEntity.badRequest().body(result);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error in build: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error building: " + e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody Map<String, Object> payload) {
        try {
            String groupName = (String) payload.get("groupName");
            double gold = payload.containsKey("gold") ? ((Number) payload.get("gold")).doubleValue() : 0;
            int beer = payload.containsKey("beer") ? ((Number) payload.get("beer")).intValue() : 0;
            int wine = payload.containsKey("wine") ? ((Number) payload.get("wine")).intValue() : 0;
            int grain = payload.containsKey("grain") ? ((Number) payload.get("grain")).intValue() : 0;
            int tools = payload.containsKey("tools") ? ((Number) payload.get("tools")).intValue() : 0;
            boolean success = holdfastManagerService.withdraw(groupName, gold, beer, wine, grain, tools);
            if (!success) {
                return ResponseEntity.badRequest().body("Insufficient resources");
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Resources withdrawn successfully"));
        } catch (Exception e) {
            System.err.println("Error in withdraw: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error withdrawing: " + e.getMessage());
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> payload) {
        try {
            String groupName = (String) payload.get("groupName");
            double gold = ((Number) payload.get("gold")).doubleValue();
            if (gold <= 0) {
                return ResponseEntity.badRequest().body("Gold amount must be positive");
            }
            Holdfast h = holdfastManagerService.deposit(groupName, gold);
            if (h == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(h);
        } catch (Exception e) {
            System.err.println("Error in deposit: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error depositing: " + e.getMessage());
        }
    }

    @PostMapping("/import")
    public ResponseEntity<?> importHoldfast(@RequestBody Holdfast holdfast) {
        try {
            if (holdfast.getGroupName() == null || holdfast.getGroupName().isBlank()) {
                return ResponseEntity.badRequest().body("groupName is required");
            }
            if (holdfastManagerService.getHoldfast(holdfast.getGroupName()) != null) {
                return ResponseEntity.badRequest().body("Holdfast already exists for group: " + holdfast.getGroupName());
            }
            Holdfast result = holdfastManagerService.importHoldfast(holdfast);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error in importHoldfast: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error importing holdfast: " + e.getMessage());
        }
    }

    @GetMapping("/{groupName}/events")
    public ResponseEntity<?> getEvents(@PathVariable String groupName) {
        try {
            List<Map<String, Object>> events = holdfastManagerService.getEvents(groupName);
            if (events == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{groupName}")
    public ResponseEntity<?> deleteHoldfast(@PathVariable String groupName) {
        try {
            if (holdfastManagerService.getHoldfast(groupName) == null) {
                return ResponseEntity.notFound().build();
            }
            holdfastManagerService.deleteHoldfast(groupName);
            return ResponseEntity.ok(Map.of("success", true, "message", "Holdfast deleted"));
        } catch (Exception e) {
            System.err.println("Error in deleteHoldfast: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error deleting holdfast: " + e.getMessage());
        }
    }
}
