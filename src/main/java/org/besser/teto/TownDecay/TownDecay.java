package org.besser.teto.TownDecay;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

import static org.besser.teto.DIETLogger.*;

public class TownDecay {
    private final JavaPlugin plugin;

    public TownDecay(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void runDecayCheckAndRuin() {
        for (Town town : TownyAPI.getInstance().getTowns()) {
            try {
                Resident mayor = town.getMayor();
                OfflinePlayer mayorOffline = Bukkit.getOfflinePlayer(mayor.getUUID());

                long lastPlayed = mayorOffline.getLastPlayed();
                long daysSince = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastPlayed);

                if (daysSince >= 90 && !town.isRuined()) {
                    log(INFO, "Ruining town '" + town.getName() + "' because mayor '" + mayor.getName() + "' has been inactive for " + daysSince + " days.");
                    town.setRuined(true);
                    town.setRuinedTime(System.currentTimeMillis());
                }

            } catch (Exception e) {
                log(WARNING, "TECO Town Decay failed to check/ruin town: " + e.getMessage());
            }
        }
    }
}
