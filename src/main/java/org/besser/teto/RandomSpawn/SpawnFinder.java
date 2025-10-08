package org.besser.teto.RandomSpawn;

import org.bukkit.Location;
import org.bukkit.Material;
import com.palmergames.bukkit.towny.TownyAPI;
import org.bukkit.Tag;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import static org.besser.teto.DIETLogger.*;

public class SpawnFinder {
    private static final Set<Material> allowedBlocks = new HashSet<>();
    private static final Set<Tag<Material>> allowedTags = new HashSet<>();

    private static int borderMinX;
    private static int borderMaxX;
    private static int borderMinZ;
    private static int borderMaxZ;

    public static void loadConfig(FileConfiguration config) {
        allowedBlocks.clear();
        allowedTags.clear();

        // Allowed blocks to spawn on
        List<String> blockNames = config.getStringList("random-spawns.allowed-spawn-blocks");
        for (String name : blockNames) {
            try {
                Material mat = Material.valueOf(name.toUpperCase());
                allowedBlocks.add(mat);
            } catch (IllegalArgumentException e) {
                log(WARNING, "[Random spawn] Invalid block material in config.yml: " + name);
            }
        }

        List<String> tagNames = config.getStringList("random-spawns.allowed-spawn-tags");
        for (String name : tagNames) {
            try {
                Tag<Material> tag = (Tag<Material>) Tag.class.getField(name.toUpperCase()).get(null);
                allowedTags.add(tag);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log(WARNING, "[Random spawn] Invalid tag in config.yml: " + name);
            }
        }

        // World border rectangle
        int swX = config.getInt("random-spawns.world-border.sw-corner.x");
        int swZ = config.getInt("random-spawns.world-border.sw-corner.z");

        int neX = config.getInt("random-spawns.world-border.ne-corner.x");
        int neZ = config.getInt("random-spawns.world-border.ne-corner.z");

        borderMinX = Math.min(swX, neX);
        borderMaxX = Math.max(swX, neX);

        borderMinZ = Math.min(swZ, neZ);
        borderMaxZ = Math.max(swZ, neZ);
    }

    /**
     * Checks if a given location is safe to spawn a player.
     */
    public static boolean isSafe(Location spawnLoc) {
        if (spawnLoc == null) return false;

        int x = spawnLoc.getBlockX();
        int z = spawnLoc.getBlockZ();
        int y = spawnLoc.getBlockY();

        // The block your feet are standing on
        Material belowType = spawnLoc.clone().subtract(0, 1, 0).getBlock().getType();
        TownyAPI towny = TownyAPI.getInstance();

        // TODO profile method to make sure it doesnt take forever.

        // Checks might be expensive, so should be run in order from
        // most likely to fail to least likely to fail.

        // Not water (rule out oceans quickly)
        if (belowType == Material.WATER) return false;

        // Within world border
        if (x < borderMinX || x > borderMaxX || z < borderMinZ || z > borderMaxZ) {
            return false;
        }

        // Above Y60, below Y130
        if (y < 61 || y > 130) return false;

        // In the wilderness (not in a town)
        boolean isWilderness = towny.isWilderness(spawnLoc);
        if (!isWilderness) return false;

        // Valid blocks
        if (!allowedBlocks.contains(belowType) && allowedTags.stream().noneMatch(tag -> tag.isTagged(belowType))) {
            return false;
        }

        return true;
    }
}
