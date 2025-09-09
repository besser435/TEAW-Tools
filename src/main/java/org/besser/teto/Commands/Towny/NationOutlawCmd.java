package org.besser.teto.Commands.Towny;

import com.palmergames.bukkit.towny.exceptions.TownyException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import org.besser.teto.Commands.BaseCommand;

import java.util.Collections;
import java.util.List;

public class NationOutlawCmd extends BaseCommand implements TownyCommandAdapter.TabCompletable {
    public NationOutlawCmd() {
        super("outlaw",
            "teto.towny.nation.outlaw",    // horrible
            "Outlaw a player across all towns in the nation. Must be a nation leader to run.",
            "/n outlaw <player>",
            true,
            false
        );
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // Ensure command is valid
        if (!isPlayer(sender)) {
            sendError(sender, "Command must be run by a player.");
            return true;
        }

        if (!hasPermission(sender)) {   // This should not happen, as everyone should have permissions.
            sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "/n " + getName() + ChatColor.WHITE + " - " + getDescription());
            sender.sendMessage(ChatColor.GRAY + "Usage: " + getUsage());
            return true;
        }

        Resident targetResident = TownyAPI.getInstance().getResident(args[0]);

        if (targetResident == null) {
            sendError(sender, "Player '" + args[0] + "' could not be found in Towny.");
            return true;
        }

        // Run sender checks
        Player playerSender = (Player) sender;
        Resident senderResident = TownyAPI.getInstance().getResident(playerSender.getUniqueId());

        if (senderResident == null) {
            return true;
        }

        // Fix duplicated code fragment. Make helper class?
        Nation senderNation;
        try {
            senderNation = senderResident.getNation();
        } catch (TownyException e) {
            sendError(sender, "You are not part of a nation.");
            return true;
        }

        if (!senderNation.isKing(senderResident)) {
            sendError(sender, "Only the nation leader can use this command.");
            return true;
        }

        // Loop through all towns in the nation and outlaw the target
        for (Town town : senderNation.getTowns()) {
            try {
                if (!town.hasOutlaw(targetResident)) {
                    town.addOutlaw(targetResident);
                }
            } catch (com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException e) {
                // Might happen if you outlaw a resident who is the mayor of a town in your nation
                sendError(sender, "Failed to outlaw " + targetResident.getName() + " in " + town.getName() + ". Are they a mayor in your nation?");
                // Don't return yet, try remaining towns.
            }
        }

        sendSuccess(sender, ChatColor.RED + targetResident.getName() + ChatColor.GREEN +
                " has been outlawed in all of your nation's towns.");
        return true;
    }

   @Override
    public List<String> customTownyTabComplete(CommandSender sender, String alias, String[] args) {
       if (!(sender instanceof Player player)) {    // Just in case the console gets here
           return Collections.emptyList();
       }

       String partial = args[0].toLowerCase();

        if (args.length == 1) { // Suggest player
            return TownyAPI.getInstance().getResidents().stream()
                    .map(Resident::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .limit(50)
                    .toList();
        }
        return Collections.emptyList();
    }
}
