package org.besser.teto;

import org.besser.teto.Commands.CommandHandler;
import org.besser.teto.TownDecay.TownDecay;
import org.besser.teto.TownDecay.TownScreenListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import static org.besser.teto.DIETLogger.*;

public final class Teto extends JavaPlugin {

    @Override
    public void onEnable() {
        DIETLogger.initialize(this);

        saveDefaultConfig();    // Fails silently if the config.yml already exists.

        boolean isEnabledInConfig = getConfig().getBoolean("teto.enable", true);
        if (!isEnabledInConfig) {
            log(WARNING, "TEAW Tools is disabled in config.yml and will not start.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        log(INFO, ChatColor.AQUA + "TEAW Tools " + ChatColor.GOLD + "v" + getDescription().getVersion() + ChatColor.RESET + " started!");


        // Town Decay
        TownDecay decay = new TownDecay(this);
        getCommand("decaycheck").setExecutor(new CommandHandler(decay));
        getServer().getPluginManager().registerEvents(new TownScreenListener(), this);
    }


    @Override
    public void onDisable() {
        log(INFO, ChatColor.AQUA + "TEAW Tools " + ChatColor.GOLD + "v" + getDescription().getVersion() + ChatColor.RESET + " stopped!");
    }
}
