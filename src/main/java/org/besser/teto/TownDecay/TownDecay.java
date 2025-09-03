package org.besser.teto.TownDecay;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Resident;
import org.besser.teto.Teto;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.TimeUnit;

import static org.besser.teto.DIETLogger.*;

public record TownDecay(Teto plugin) {
    public int runDecayCheckAndRuin() {
        int townsDecayed = 0;

        for (Town town : TownyAPI.getInstance().getTowns()) {
            try {
                Resident mayor = town.getMayor();
                OfflinePlayer mayorOffline = Bukkit.getOfflinePlayer(mayor.getUUID());

                long lastPlayed = mayorOffline.getLastPlayed();
                long daysSince = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastPlayed);

                // Send warnings to Discord. Code would be cleaner if that was handled somewhere else, but oh well
                //if (daysSince == 87 && !town.isRuined()) {}   // 3 day warning.

                //if (daysSince == 88 && !town.isRuined()) {}   // 2 day warning

                //if (daysSince == 89 && !town.isRuined()) {}   // 1 day warning

                if (daysSince >= 90 && !town.isRuined()) {
                    log(INFO, "[Town decay] Ruining town '" + town.getName() + "' mayor '" + mayor.getName() + "' inactive for " + daysSince + " days");
                    town.setRuined(true);
                    town.setRuinedTime(System.currentTimeMillis());
                    townsDecayed++;

                    town.save();    // Does this fix the saving issue?
                }
            } catch (Exception e) {
                log(WARNING, "[Town decay] Town Decay failed: " + e.getMessage());
            }
        }

        return townsDecayed;
    }
}
