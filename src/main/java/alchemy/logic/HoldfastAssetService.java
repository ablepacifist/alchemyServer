package alchemy.logic;

import java.nio.file.Path;
import java.util.Map;

/**
 * Holdfast images and their emoji fallbacks.
 *
 * <p>Clients ask for the manifest once and render whatever it says: an image when an entry
 * has a {@code url}, otherwise its {@code emoji}. Adding art later is a file drop plus one line
 * in {@code manifest.json}; no client rebuild or server restart.
 */
public interface HoldfastAssetService {

    /** Path prefix that the asset files are served under. */
    String URL_PREFIX = "/api/holdfast/assets/";

    /**
     * {@code {version, assets: {key: {label, emoji, url?}}}}. {@code url} is present only when
     * the manifest names a file that really exists, and is relative to the Alchemy server.
     */
    Map<String, Object> getManifest();

    /** A servable asset file, or {@code null} when the category or name is not allowed. */
    Path getAssetFile(String category, String fileName);

    /** The Content-Type to serve {@code file} with. */
    String contentType(Path file);
}
