package org.besser.teto;

import com.earth2me.essentials.Essentials;

import org.besser.teto.Commands.CommandManager;
import org.besser.teto.RandomSpawn.RandomSpawn;
import org.besser.teto.RandomSpawn.SpawnFinder;

import org.besser.teto.Commands.Towny.MapColor.TownColorChangeListener;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static org.besser.teto.DIETLogger.*;

public final class Teto extends JavaPlugin {
    private static Essentials essentials = null;

    @Override
    public void onEnable() {
        DIETLogger.initialize(this);

        saveDefaultConfig();    // Fails silently if the config.yml already exists.


        // debug to try to find the right event. change later.
        getServer().getPluginManager().registerEvents(new TownColorChangeListener(), this);


        boolean isEnabledInConfig = getConfig().getBoolean("teto.enabled", true);
        if (!isEnabledInConfig) {
            log(WARNING, "TEAW Tools is disabled in config.yml and will not start.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!setupEssentials()) {
            log(SEVERE, "Required dependencies are missing. TETO will not start.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Random spawns
        boolean isRandomSpawnsEnabled = getConfig().getBoolean("random-spawns.enabled", true);
        if (isRandomSpawnsEnabled) {
            SpawnFinder.loadConfig(getConfig());
            RandomSpawn randomSpawn = new RandomSpawn(this, essentials);
        }

        // Set up commands (reminder, needs to happen after objects are initialized)
        CommandManager commandManager = new CommandManager(this);
        commandManager.registerCommands();
        log(INFO, ChatColor.AQUA + "TEAW Tools " + ChatColor.GOLD + "v" + getDescription().getVersion() + ChatColor.RESET + " started!");
    }

    // Setup depends
    private boolean setupEssentials() {
        Plugin essentialsPlugin = getServer().getPluginManager().getPlugin("Essentials");
        if (essentialsPlugin instanceof Essentials) {
            essentials = (Essentials) essentialsPlugin;
            return true;
        } else {
            log(SEVERE, "Essentials plugin not found!");
            return false;
        }
    }

    // Getters
//    public static Essentials getEssentials() {    // Unused
//        return essentials;
//    }

    @Override
    public void onDisable() {
        log(INFO, ChatColor.AQUA + "TEAW Tools " + ChatColor.GOLD + "v" + getDescription().getVersion() + ChatColor.RESET + " stopped!");
    }
}
