package org.besser.teto;

import com.earth2me.essentials.Essentials;
import org.besser.teto.AntiCheat.NoInvisListener;
import org.besser.teto.Commands.CommandManager;
import org.besser.teto.Commands.Towny.MapColor.TownColorChangeListener;
import org.besser.teto.Commands.Towny.RequireNation.ForceNationListener;
import org.besser.teto.EnderPearlNerf.EnderPearlCooldown;
import org.besser.teto.RandomSpawn.RandomSpawn;
import org.besser.teto.RandomSpawn.SpawnSafeVerifier;
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
            SpawnSafeVerifier.loadConfig(getConfig());
            new RandomSpawn(this, essentials);
        } else {
            log(WARNING, "TETO random spawns are disabled. TETO will not handle player spawns.");
        }

        // Set up commands (reminder, needs to happen after objects are initialized)
        CommandManager commandManager = new CommandManager(this);
        commandManager.registerCommands();
        log(INFO, ChatColor.AQUA + "TEAW Tools " + ChatColor.GOLD + "v" + getDescription().getVersion() + ChatColor.RESET + " started!");

        // Set up listeners
        getServer().getPluginManager().registerEvents(new TownColorChangeListener(), this);
        getServer().getPluginManager().registerEvents(new ForceNationListener(this), this);
        getServer().getPluginManager().registerEvents(new NoInvisListener(), this);

        boolean isPearlNerfEnabled = getConfig().getBoolean("ender-pearl-nerf.enabled", true);
        if (isPearlNerfEnabled) {
            getServer().getPluginManager().registerEvents(new EnderPearlCooldown(this), this);
        } else {
            log(WARNING, "TETO Ender Pearl nerfs are disabled.");
        }
        //getServer().getPluginManager().registerEvents(new KickBannedPlayers(), this);
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
