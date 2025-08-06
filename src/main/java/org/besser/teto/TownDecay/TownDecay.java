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

    // TODO: check that this module is enabled in config.yml. if not, skip
    // TODO: should be called on every new towny day. send alerts at 87, 88, and 89 days, and delete on the 90th day.
    public void runDecayCheckAndRuin() {
        for (Town town : TownyAPI.getInstance().getTowns()) {
            try {
                Resident mayor = town.getMayor();
                OfflinePlayer mayorOffline = Bukkit.getOfflinePlayer(mayor.getUUID());

                long lastPlayed = mayorOffline.getLastPlayed();
                long daysSince = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastPlayed);

                if (daysSince >= 90 && !town.isRuined()) {
                    log(INFO, "[Town decay] Ruining town '" + town.getName() + "' because mayor '" + mayor.getName() + "' has been inactive for " + daysSince + " days.");
                    town.setRuined(true);
                    town.setRuinedTime(System.currentTimeMillis());
                }

                //town.save();    // Does this fix the saving issue?

                // BUG: the ruined status might not be saved. there is a manual save command, use that

            } catch (Exception e) {
                log(WARNING, "[Town decay] Town Decay failed to check/ruin town: " + e.getMessage());
            }
        }
    }
}
