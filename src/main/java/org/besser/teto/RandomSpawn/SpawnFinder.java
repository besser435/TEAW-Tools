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

        // The block your feet are standing on
        Material belowType = spawnLoc.clone().subtract(0, 1, 0).getBlock().getType();
        TownyAPI towny = TownyAPI.getInstance();

        // debuggy reference
        log(INFO, "(debug) spawnBlockMaterialType " + belowType);   // TODO maybe rename the variable to this
        log(INFO, "(debug) Checking loc: " + spawnLoc.getX() + " " + spawnLoc.getZ());

        // TODO profile method to make sure it doesnt take forever.

        // Checks might be expensive, so should be run in order from
        // most likely to fail to least likely to fail

        // Water presence (rule out oceans quickly fr)
        if (belowType == Material.WATER) return false;

        // Within world border

        // Above Y60, below Y130
        if (spawnLoc.getY() < 61 || spawnLoc.getY() > 130) return false;

        // Tree/logs within 160 blocks

        // In the wilderness (not in a town)
        boolean isWilderness = towny.isWilderness(spawnLoc);
        if (!isWilderness) return false;

        // Valid blocks (grass, logs, leaves, dirt, coarse dirt, sand, red sand, other crap)
        // use belowType

        // Sky is visible (should always be true given this method is called with getHighestBlockYAt())


        return true;
    }
}
