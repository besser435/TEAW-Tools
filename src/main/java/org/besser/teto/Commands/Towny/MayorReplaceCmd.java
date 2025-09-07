package org.besser.teto.Commands.Towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.besser.teto.Commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.besser.teto.DIETLogger.*;

public class MayorReplaceCmd extends BaseCommand implements TownyCommandAdapter.TabCompletable {

    public MayorReplaceCmd() {
        super("replace",
            "teto.towny.nation.mayor_replace",
            "Replace the mayor of a town with another resident.",
            "/n mayor replace <town> <resident>",
            true,
            false
        );
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // Ensure command is valid
        if (!isPlayer(sender)) {
            sendError(sender, "Only players can run this command.");
            return true;
        }

        if (!hasPermission(sender)) {   // This should not happen, as everyone should have permissions.
            sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "/n " + getName() + ChatColor.WHITE + " - " + getDescription());
            sender.sendMessage(ChatColor.GRAY + "Usage: " + getUsage());
            return true;
        }

        String townName = args[0];
        String newMayorName = args[1];

        // Run args checks
        Player playerSender = (Player) sender;
        Resident senderResident = TownyAPI.getInstance().getResident(playerSender.getUniqueId());

        if (senderResident == null) {
            return true;
        }

        Town town = TownyAPI.getInstance().getTown(townName);
        if (town == null) {
            sendError(sender, "Town '" + townName + "' not found.");
            return true;
        }

        Resident newMayor = TownyAPI.getInstance().getResident(newMayorName);
        if (newMayor == null) {
            sendError(sender, "Resident '" + newMayorName + "' not found.");
            return true;
        }

        // Ensure sender is a nation leader
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

        // Ensure the target resident is in the town
        // TODO: tab completer should only return residents in the town.
        if (!newMayor.hasTown() || !Objects.equals(newMayor.getTownOrNull(), town)) {
            sendError(sender, "Resident '" + newMayorName + "' is not a member of town " + town.getName() + ".");
            return true;
        }

        // Swap mayor
        Resident oldMayor = town.getMayor();
        try {
            town.setMayor(newMayor);
        } catch (Exception e) {
            sendError(sender, "Failed to replace mayor. If this issue persists, please report it to an admin.");
            log(WARNING, "Failed to replace mayor: " + e.getMessage());
            return true;
        }

        sendSuccess(sender, "Mayor of " + ChatColor.AQUA + town.getName() + ChatColor.GREEN +
                " has been changed from " + oldMayor.getName() + " to " + newMayor.getName() + ".");
        return true;
    }

    @Override
    public List<String> customTownyTabComplete(CommandSender sender, String alias, String[] args) {
        String partial = args[0].toLowerCase();
        Town town = TownyAPI.getInstance().getTown(args[1]);    // might be null

        // intelliJ says Condition 'args. length == 1' is always 'false'
        if (args.length == 1) {     // Town
            return TownyAPI.getInstance().getTowns().stream()
                    .map(Town::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .limit(50)
                    .toList();

        } else if (args.length == 2) {      // New mayor
            if (town == null) return Collections.emptyList();

            return town.getResidents().stream()
                    .map(Resident::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .limit(50)
                    .toList();

        }
        return Collections.emptyList();
    }
}
