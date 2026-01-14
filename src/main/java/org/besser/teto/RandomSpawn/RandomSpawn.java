package org.besser.teto.RandomSpawn;

import com.earth2me.essentials.Essentials;
import org.besser.teto.Teto;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import static org.besser.teto.DIETLogger.*;

import java.io.File;
import java.util.List;
import java.util.Random;

public class RandomSpawn implements Listener {
    private final Teto plugin;
    private final Essentials essentials;
    private final YamlConfiguration spawnConfig;
    private final World world;
    private boolean essOverriding = false;

    public RandomSpawn(Teto plugin, Essentials ess) {
        this.plugin = plugin;
        this.world = Bukkit.getWorlds().get(0); // Might cause issues if using BungeeCord. Should use getWorld({world name set in config})
        this.essentials = ess;

        File configFile = new File(plugin.getDataFolder(), "random_spawn_locations.yml");
        if (!configFile.exists()) {
            plugin.getLogger().info("[Random spawn] No spawn file found, generating random_spawn_locations.yml");
            plugin.saveResource("random_spawn_locations.yml", false);
        }

        this.spawnConfig = YamlConfiguration.loadConfiguration(configFile);

        EventPriority essSpawnJoinPriority = essentials.getSettings().getSpawnJoinPriority();
        if (!(essSpawnJoinPriority == null)) {
            log(SEVERE, "[Random spawn] Essentials is overriding spawn join priority (" + essSpawnJoinPriority + "). TETO random spawns will not work.");
            this.essOverriding = true;
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // TODO: also handle destroyed beds or unset spawn location

    /** Essentials is bad! it does not respect listener priority. As such, for this to work you MUST set
     `respawn-listener-priority` to `none` and `spawn-join-listener-priority` to `none` in the config. Even with this
     method running at HIGHEST, Essentials still does not allow it to work. There is no way to change this in code,
     so it must be done with the config file.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFirstJoin(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();

        if (player.hasPlayedBefore()) return;

        Location randomSpawn = getRandomSpawnLocation();
        if (randomSpawn != null) {
            event.setSpawnLocation(randomSpawn);

            if (essOverriding) {log(WARNING, "[Random spawn] Essentials may override random spawn");}

            log(INFO, "[Random spawn] Spawning " + player.getName() + " at " +
                    randomSpawn.getX() + " " + randomSpawn.getY() + " " + randomSpawn.getZ());
        } else {
           log(SEVERE, "[Random spawn] Failed to find a valid spawn location!");
        }
    }

    private Location getRandomSpawnLocation() {
        List<?> spawnList = spawnConfig.getList("spawn_locations");

        if (spawnList == null || spawnList.isEmpty()) {
            log(SEVERE,"[Random spawn] No spawn locations defined!");
            return null;
        }

        Random random = new Random();

        for (int i = 0; i < spawnList.size(); i++) {
            // TODO still using debug locations.
            // change to like 20 max iterations or something depending on how long each iter takes.
            int index = random.nextInt(spawnList.size());
            Object entry = spawnList.get(index);

            if (entry instanceof java.util.Map<?, ?> map) {
                try {
                    int x = (int) map.get("x");
                    int z = (int) map.get("z");
                    int y = world.getHighestBlockYAt(x, z);

                    Location spawnLoc = new Location(world, x + 0.5, y + 1, z + 0.5);

                    long startTime = System.currentTimeMillis();

                    if (!SpawnSafeVerifier.isSafe(spawnLoc)) continue;

                    long endTime = System.currentTimeMillis();
                    log(INFO, "[Random spawn] Took " + (endTime - startTime) + "ms to verify spawn candidate");
                    // Note this does not include the total time to find a spawn, just the one check.

                    return spawnLoc;
                } catch (Exception e) {
                   log(WARNING,"[Random spawn] Invalid spawn entry in YAML: " + entry);
                }
            }
        }

        return null;
    }
}
