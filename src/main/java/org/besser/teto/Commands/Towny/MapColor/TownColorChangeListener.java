package org.besser.teto.Commands.Towny.MapColor;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TownColorChangeListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase().trim();

        // This is kind of janky because it's a hard coded string, but whatever, it's the best we can do with Towny's API.
        boolean isMapColor = msg.startsWith("/t set mapcolor") || msg.startsWith("/town set mapcolor");
        if (!isMapColor) return;

        Player player = event.getPlayer();
        Resident resident = TownyAPI.getInstance().getResident(player);

        // Ensure everything is valid
        if (resident == null || !resident.hasTown() || !resident.hasNation()) return;
        Nation nation = resident.getNationOrNull();
        if (nation == null) return;

        // Lock check and messages
        boolean isLocked = false;
        if (nation.hasMeta("teto_mapcolor_locked")) {
            BooleanDataField lockField = (BooleanDataField) nation.getMetadata("teto_mapcolor_locked");
            if (lockField != null && lockField.getValue()) {
                isLocked = true;
            }
        }
        if (!isLocked) return;

        event.setCancelled(true);

        if (nation.getKing().equals(resident)) {
            player.sendMessage(ChatColor.RED + "You have disabled individual town map color changes. " +
                    "Change them globally with /n mapcolor set <color> instead.");
        } else {
            player.sendMessage(ChatColor.RED + "Your nation has locked town map color changes. " +
                    "Only the nation leader can change colors.");
        }
    }
}
