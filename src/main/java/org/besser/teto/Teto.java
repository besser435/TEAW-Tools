package org.besser.teto;

import com.earth2me.essentials.Essentials;
import org.besser.teto.Commands.CommandHandler;
import org.besser.teto.RandomSpawn.RandomSpawn;
import org.besser.teto.TownDecay.TownDecay;
import org.besser.teto.TownDecay.TownDecayListener;
import org.besser.teto.TownDecay.TownScreenListener;
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

        log(INFO, ChatColor.AQUA + "TEAW Tools " + ChatColor.GOLD + "v" + getDescription().getVersion() + ChatColor.RESET + " started!");


        // Town Decay
        boolean isTownDecayEnabled = getConfig().getBoolean("town-decay.enabled", true);
        if (isTownDecayEnabled) {
            TownDecay townDecay = new TownDecay(this);
            getServer().getPluginManager().registerEvents(new TownDecayListener(townDecay), this);
            //getCommand("decaycheck").setExecutor(new CommandHandler(decay));
            getServer().getPluginManager().registerEvents(new TownScreenListener(), this);
        }

        // Random spawns
        boolean isRandomSpawnsEnabled = getConfig().getBoolean("random-spawns.enabled", true);
        if (isRandomSpawnsEnabled) {
            RandomSpawn randomSpawn = new RandomSpawn(this, essentials);
            getServer().getPluginManager().registerEvents(randomSpawn, this);
        }
    }

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

    @Override
    public void onDisable() {
        log(INFO, ChatColor.AQUA + "TEAW Tools " + ChatColor.GOLD + "v" + getDescription().getVersion() + ChatColor.RESET + " stopped!");
    }
}
