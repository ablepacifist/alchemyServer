package alchemy.api;

import alchemy.logic.HoldfastAssetService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

/** Serves Holdfast images and the manifest that maps asset keys to them. */
@RestController
@RequestMapping("/api/holdfast/assets")
@CrossOrigin(origins = "*")
public class HoldfastAssetController {

    private final HoldfastAssetService assetService;

    public HoldfastAssetController(HoldfastAssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/manifest")
    public ResponseEntity<Map<String, Object>> manifest() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(assetService.getManifest());
    }

    @GetMapping("/{category}/{fileName:.+}")
    public ResponseEntity<FileSystemResource> asset(@PathVariable String category, @PathVariable String fileName) {
        Path file = assetService.getAssetFile(category, fileName);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                .contentType(MediaType.parseMediaType(assetService.contentType(file)))
                // An SVG opened directly must not be able to run script on this origin.
                .header("Content-Security-Policy", "default-src 'none'; style-src 'unsafe-inline'; img-src data:")
                .header("X-Content-Type-Options", "nosniff")
                .body(new FileSystemResource(file));
    }
}
