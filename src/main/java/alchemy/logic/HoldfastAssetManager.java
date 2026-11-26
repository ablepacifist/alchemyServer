package alchemy.logic;

import alchemy.data.HoldfastAssetStore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Reads the Holdfast asset manifest, re-reading it only when the file changes, and decides
 * which asset files may be served.
 */
@Service
public class HoldfastAssetManager implements HoldfastAssetService {

    /** Folders under the asset root that may be served. */
    static final Set<String> CATEGORIES = Set.of("buildings", "resources", "crops", "castles", "tabs", "ui");

    private static final Map<String, String> TYPES = Map.of(
            "png", "image/png",
            "webp", "image/webp",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "gif", "image/gif",
            "svg", "image/svg+xml");

    private static final ObjectMapper JSON = new ObjectMapper();

    private final HoldfastAssetStore store;

    private Map<String, Object> cached = emptyManifest();
    private long cachedModified = Long.MIN_VALUE;

    public HoldfastAssetManager(HoldfastAssetStore store) {
        this.store = store;
    }

    @Override
    public synchronized Map<String, Object> getManifest() {
        long modified = store.manifestModified();
        if (modified != cachedModified) {
            cached = build(store.readManifest());
            cachedModified = modified;
        }
        return cached;
    }

    @Override
    public Path getAssetFile(String category, String fileName) {
        if (category == null || !CATEGORIES.contains(category) || extensionType(fileName) == null) {
            return null;
        }
        return store.resolve(category, fileName);
    }

    @Override
    public String contentType(Path file) {
        String type = extensionType(file.getFileName().toString());
        return type != null ? type : "application/octet-stream";
    }

    private Map<String, Object> build(byte[] raw) {
        if (raw == null) {
            return emptyManifest();
        }
        Map<String, Object> assets = new LinkedHashMap<>();
        int version = 1;
        try {
            JsonNode root = JSON.readTree(raw);
            version = root.path("version").asInt(1);
            Iterator<Map.Entry<String, JsonNode>> it = root.path("assets").fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> e = it.next();
                JsonNode node = e.getValue();
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("label", node.path("label").asText(e.getKey()));
                entry.put("emoji", node.path("emoji").asText(""));
                String url = urlFor(node.path("file").isTextual() ? node.path("file").asText() : null);
                if (url != null) {
                    entry.put("url", url);
                }
                assets.put(e.getKey(), entry);
            }
        } catch (Exception e) {
            // A hand-edited manifest with a typo must not take the Holdfast page down:
            // serve what parsed so far and let the clients fall back to emoji.
            System.err.println("Holdfast asset manifest could not be read: " + e.getMessage());
        }
        Map<String, Object> manifest = new LinkedHashMap<>();
        manifest.put("version", version);
        manifest.put("assets", assets);
        return manifest;
    }

    /** The served URL for a manifest {@code file} value, or {@code null} if it is not servable. */
    private String urlFor(String file) {
        if (file == null || file.isBlank()) {
            return null;
        }
        String[] parts = file.replace('\\', '/').split("/");
        if (parts.length != 2) {
            return null;
        }
        Path path = getAssetFile(parts[0], parts[1]);
        if (path == null) {
            return null;
        }
        // The version stamp lets clients cache images for a day yet still see a replaced file.
        return URL_PREFIX + parts[0] + "/" + parts[1] + "?v=" + store.modified(path);
    }

    private static String extensionType(String fileName) {
        if (fileName == null) {
            return null;
        }
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? null : TYPES.get(fileName.substring(dot + 1).toLowerCase(Locale.ROOT));
    }

    private static Map<String, Object> emptyManifest() {
        Map<String, Object> manifest = new LinkedHashMap<>();
        manifest.put("version", 1);
        manifest.put("assets", Map.of());
        return manifest;
    }
}
