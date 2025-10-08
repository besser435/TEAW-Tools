package org.besser.teto.Commands.Towny.MapColor;


import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.event.TownBlockSettingsChangedEvent;
import com.palmergames.bukkit.towny.event.town.TownMapColourNationalCalculationEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Objects;

import static org.besser.teto.DIETLogger.*;




// If the mayor of a town changes the map color and the nation does not allow it, cancel the event.
// These are debug listeners to try to find the right event.
public class TownColorChangeListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        // This is kind of janky because it's a hard coded string, but whatever, it's the best we can do with Towny's API.
        String msg = event.getMessage().toLowerCase();

        if (msg.startsWith("/t set mapcolor")) {
            // Check if their nation has "mapcolor_locked"
            Player player = event.getPlayer();
            Resident res = TownyAPI.getInstance().getResident(player);

            if (res == null || !res.hasTown()) return;

            Nation nation = res.hasNation() ? res.getNationOrNull() : null;

            if (nation != null && nation.hasMeta("mapcolor_locked")) {
                // This method doesn't test if the sender has perms to change the map color.
                // The error will be shown to anyone who runs it.
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Your nation has locked map color changes. Only the nation leader can change colors.");
            }
        }
    }

}
