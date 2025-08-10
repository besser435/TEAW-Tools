package org.besser.teto.RandomSpawn;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;
import org.bukkit.Material;
import com.palmergames.bukkit.towny.TownyAPI;

import static org.besser.teto.DIETLogger.*;

public class SpawnFinder {

    /**
     * Checks if a given location is safe to spawn a player.
     */
    public static boolean isSafe(Location spawnLoc) {
        if (spawnLoc == null) return false;

        Material blockType = spawnLoc.getBlock().getType(); // TODO might not be needed given variable below // Head
        Material belowType = spawnLoc.clone().subtract(0, 1, 0).getBlock().getType(); // Feet
        log(WARNING, "is feet test " + spawnLoc.clone().subtract(0, 1, 0).getY()); // confirms above really is feet
        TownyAPI towny = TownyAPI.getInstance();

        // debuggy reference
        log(INFO, "BlockType: " + blockType + " belowType: " + belowType );
        log(INFO, "Checking loc: " + spawnLoc.getX() + " " + spawnLoc.getZ());




        // Checks might be expensive, so should be run in order from
        // most likely to fail to least likely to fail

        // Water presence
        if (blockType == Material.WATER || belowType == Material.WATER) return false;

        // Within world border

        // Tree/logs within 160 blocks

        // In the wilderness (not in a town)
        boolean isWilderness = towny.isWilderness(spawnLoc);
        if (!isWilderness) return false;

        // Valid blocks (grass, logs, leaves, dirt, coarse dirt, sand, red sand, other crap)

        // Sky is visible (should always be true given this method is called with getHighestBlockYAt())





        return true;
    }
}
