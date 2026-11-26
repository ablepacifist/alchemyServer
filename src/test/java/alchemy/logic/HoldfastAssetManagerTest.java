package alchemy.logic;

import alchemy.data.HoldfastAssetStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HoldfastAssetManagerTest {

    @TempDir
    Path root;

    private HoldfastAssetManager manager;

    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectories(root.resolve("buildings"));
        Files.write(root.resolve("buildings/granary.png"), new byte[]{1, 2, 3});
        writeManifest("{\"version\":1,\"assets\":{"
                + "\"building.granary\":{\"label\":\"Granary\",\"emoji\":\"G\",\"file\":\"buildings/granary.png\"},"
                + "\"building.mine\":{\"label\":\"Mine\",\"emoji\":\"M\",\"file\":\"buildings/missing.png\"},"
                + "\"resource.wood\":{\"label\":\"Wood\",\"emoji\":\"W\",\"file\":null},"
                + "\"ui.sneaky\":{\"label\":\"x\",\"emoji\":\"x\",\"file\":\"../manifest.json\"}}}", 1000);
        manager = new HoldfastAssetManager(new HoldfastAssetStore(root.toString()));
    }

    private void writeManifest(String json, long modifiedMillis) throws Exception {
        Path manifest = root.resolve("manifest.json");
        Files.writeString(manifest, json, StandardCharsets.UTF_8);
        Files.setLastModifiedTime(manifest, FileTime.fromMillis(modifiedMillis));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> entry(String key) {
        return (Map<String, Object>) ((Map<String, Object>) manager.getManifest().get("assets")).get(key);
    }

    @Test
    void existingFileGetsAVersionedUrl() {
        String url = (String) entry("building.granary").get("url");
        assertNotNull(url);
        assertTrue(url.startsWith("/api/holdfast/assets/buildings/granary.png?v="));
        assertEquals("G", entry("building.granary").get("emoji"));
    }

    @Test
    void missingOrUnsetFileFallsBackToEmojiOnly() {
        assertNull(entry("building.mine").get("url"));
        assertNull(entry("resource.wood").get("url"));
        assertEquals("W", entry("resource.wood").get("emoji"));
    }

    @Test
    void manifestCannotPointOutsideTheAssetFolder() {
        assertNull(entry("ui.sneaky").get("url"));
    }

    @Test
    void refusesTraversalUnknownCategoriesAndNonImages() throws Exception {
        assertNotNull(manager.getAssetFile("buildings", "granary.png"));
        assertNull(manager.getAssetFile("buildings", "../manifest.json"));
        assertNull(manager.getAssetFile("..", "manifest.json"));
        assertNull(manager.getAssetFile("secrets", "granary.png"));
        Files.writeString(root.resolve("buildings/notes.txt"), "hi");
        assertNull(manager.getAssetFile("buildings", "notes.txt"));
    }

    @Test
    void reloadsWhenTheManifestChanges() throws Exception {
        assertNull(entry("resource.wood").get("url"));
        Files.createDirectories(root.resolve("resources"));
        Files.write(root.resolve("resources/wood.webp"), new byte[]{9});
        writeManifest("{\"version\":2,\"assets\":{"
                + "\"resource.wood\":{\"label\":\"Wood\",\"emoji\":\"W\",\"file\":\"resources/wood.webp\"}}}", 2000);
        assertEquals(2, manager.getManifest().get("version"));
        assertNotNull(entry("resource.wood").get("url"));
        assertEquals("image/webp", manager.contentType(root.resolve("resources/wood.webp")));
    }

    @Test
    void brokenManifestServesEmptyInsteadOfFailing() throws Exception {
        writeManifest("{ not json", 3000);
        Map<String, Object> manifest = manager.getManifest();
        assertNotNull(manifest.get("assets"));
    }

    @Test
    void shippedManifestParsesAndCoversEveryBuilding() {
        HoldfastAssetManager shipped = new HoldfastAssetManager(new HoldfastAssetStore("holdfast-assets"));
        @SuppressWarnings("unchecked")
        Map<String, Object> assets = (Map<String, Object>) shipped.getManifest().get("assets");
        for (String type : alchemy.object.BuildingConfig.BUILDING_TYPES.keySet()) {
            assertTrue(assets.containsKey("building." + type), "manifest is missing building." + type);
        }
    }
}
