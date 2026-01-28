package org.besser.teto.Commands.Towny.RequireNation;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.NewDayEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.PreDeleteNationEvent;
import com.palmergames.bukkit.towny.event.nation.NationTownLeaveEvent;
import com.palmergames.bukkit.towny.object.Town;
import org.besser.teto.Teto;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ForceNationListener implements Listener {
    private final Teto plugin;
    
    public ForceNationListener(Teto plugin) {
        this.plugin = plugin;
    }

    // Prevent deletion
    @EventHandler()
    public void handleDeleteNation(PreDeleteNationEvent event) {
        String message = ChatColor.RED +
                "All towns must have a nation, so it cannot be deleted. Perhaps rename it?";
        CommandSender sender = event.getSender();
        if (sender != null) sender.sendMessage(message);

        event.setCancelled(true);
    }

    // Let towns leave their nation, but give them a new one
    // If the town is the last in the nation, handleDeleteNation() will be called instead.
    @EventHandler()
    public void handleTownLeaveNation(NationTownLeaveEvent event) {
        Town town = event.getTown();
        
        // Event runs before the town has left, so we have to schedule creation after the event.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            CreateNationForTown.giveNation(town);
        }, 10L);
    }

    // Give to new towns
    @EventHandler()
    public void handleNewTown(NewTownEvent event) {
        Town town = event.getTown();
        CreateNationForTown.giveNation(town);
    }

    // Periodically catch any stragglers
    @EventHandler()
    public void newDayGiveNation(NewDayEvent event) {
        for (Town town : TownyAPI.getInstance().getTowns()) {
            if (town.hasNation()) {
                continue;
            }
            CreateNationForTown.giveNation(town);
        }
    }
}
