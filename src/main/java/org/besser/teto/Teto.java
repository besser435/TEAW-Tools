package org.besser.teto;

import com.earth2me.essentials.Essentials;

import org.besser.teto.Commands.CommandManager;
import org.besser.teto.RandomSpawn.RandomSpawn;
import org.besser.teto.RandomSpawn.SpawnFinder;
import org.besser.teto.TownDecay.TownDecay;
import org.besser.teto.TownDecay.TownDecayListener;
import org.besser.teto.TownDecay.TownScreenListener;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static org.besser.teto.DIETLogger.*;

public final class Teto extends JavaPlugin {
    private static Essentials essentials = null;
    private static TownDecay townDecay = null;

    @Override
    public void onEnable() {
        DIETLogger.initialize(this);

        saveDefaultConfig();    // Fails silently if the config.yml already exists.

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

        // Town Decay
        boolean isTownDecayEnabled = getConfig().getBoolean("town-decay.enabled", false);
        if (isTownDecayEnabled) {
            townDecay = new TownDecay(this);
            getServer().getPluginManager().registerEvents(new TownDecayListener(townDecay), this);
            getServer().getPluginManager().registerEvents(new TownScreenListener(), this);
        }

        // Random spawns
        boolean isRandomSpawnsEnabled = getConfig().getBoolean("random-spawns.enabled", true);
        if (isRandomSpawnsEnabled) {
            SpawnFinder.loadConfig(getConfig());
            RandomSpawn randomSpawn = new RandomSpawn(this, essentials);
            getServer().getPluginManager().registerEvents(randomSpawn, this);
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

    public TownDecay getTownDecay() {
        return townDecay;
    }

    @Override
    public void onDisable() {
        log(INFO, ChatColor.AQUA + "TEAW Tools " + ChatColor.GOLD + "v" + getDescription().getVersion() + ChatColor.RESET + " stopped!");
    }
}
