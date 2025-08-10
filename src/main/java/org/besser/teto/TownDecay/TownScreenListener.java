package org.besser.teto.TownDecay;

import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.statusscreens.StatusScreen;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TownScreenListener implements Listener {

    private static final int DECAY_DAYS = 90;   // TODO: Update me to config

    @EventHandler
    public void onTownDisplay(TownStatusScreenEvent event) {
        Town town = event.getTown();
        if (town == null || town.getMayor() == null) return;

        StatusScreen statusScreen = event.getStatusScreen();

        OfflinePlayer mayorOffline = Bukkit.getOfflinePlayer(town.getMayor().getUUID());
        long lastPlayed = mayorOffline.getLastPlayed();

        String decayStatus;
        if (town.isRuined()) {
            decayStatus = ChatColor.RED + "Ruined since: " + formatTime(town.getRuinedTime());
        } else {
            long now = System.currentTimeMillis();
            long daysSinceLastLogin = TimeUnit.MILLISECONDS.toDays(now - lastPlayed);
            long daysUntilDecay = DECAY_DAYS - daysSinceLastLogin;

            if (daysUntilDecay <= 0) {
                decayStatus = ChatColor.RED + "Eligible for decay (mayor inactive for " + daysSinceLastLogin + " days)";
            } else {
                decayStatus = ChatColor.YELLOW + "Decays in: " + daysUntilDecay + " day" + (daysUntilDecay == 1 ? "" : "s (mayor is active)");
            }
        }

        statusScreen.addComponentOf("Decay", decayStatus);
    }

    private String formatTime(long millis) {
        if (millis <= 0) return "Unknown";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(millis));
    }
}
