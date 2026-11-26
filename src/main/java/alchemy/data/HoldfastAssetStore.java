package alchemy.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Storage layer for Holdfast images: a plain folder on disk holding {@code manifest.json}
 * and one subfolder per asset category.
 *
 * <p>The folder comes from {@code HOLDFAST_ASSETS_PATH} (default {@code ./holdfast-assets},
 * relative to the alchemyServer working directory). Only raw reads live here; deciding what
 * is a valid key or file is the logic layer's job.
 */
@Repository
public class HoldfastAssetStore {

    public static final String MANIFEST = "manifest.json";

    private final Path root;

    public HoldfastAssetStore(@Value("${HOLDFAST_ASSETS_PATH:./holdfast-assets}") String rootDir) {
        this.root = Paths.get(rootDir).toAbsolutePath().normalize();
    }

    public Path root() {
        return root;
    }

    /** Modified time of the manifest in millis, or -1 when it is missing or unreadable. */
    public long manifestModified() {
        try {
            Path manifest = root.resolve(MANIFEST);
            return Files.isReadable(manifest) ? Files.getLastModifiedTime(manifest).toMillis() : -1L;
        } catch (IOException e) {
            return -1L;
        }
    }

    /** Raw manifest bytes, or {@code null} when it is missing or unreadable. */
    public byte[] readManifest() {
        try {
            Path manifest = root.resolve(MANIFEST);
            return Files.isReadable(manifest) ? Files.readAllBytes(manifest) : null;
        } catch (IOException e) {
            return null;
        }
    }

    /** Modified time of an asset file in millis, or 0 when it cannot be read. */
    public long modified(Path file) {
        try {
            return Files.getLastModifiedTime(file).toMillis();
        } catch (IOException e) {
            return 0L;
        }
    }

    /**
     * The file at {@code segments} under the asset root, or {@code null} when the path would
     * leave the root (through "..", an absolute segment or a drive prefix) or is not a readable
     * regular file. Same rule as pokemon's {@code SafeFiles.resolveUnder}.
     */
    public Path resolve(String... segments) {
        Path resolved = root;
        try {
            for (String segment : segments) {
                if (segment == null || segment.isEmpty() || segment.indexOf('\0') >= 0) {
                    return null;
                }
                resolved = resolved.resolve(segment);
            }
            resolved = resolved.normalize();
        } catch (RuntimeException e) { // InvalidPathException on Windows for ':' or '*'
            return null;
        }
        if (!resolved.startsWith(root) || resolved.equals(root)) {
            return null;
        }
        return Files.isRegularFile(resolved) && Files.isReadable(resolved) ? resolved : null;
    }
}
