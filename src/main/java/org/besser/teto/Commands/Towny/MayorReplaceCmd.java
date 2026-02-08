package org.besser.teto.Commands.Towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
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
            "Replace the mayor of a town with another resident. Must be a nation leader to run.",
            "/n mayor replace <town> <resident>",
            true,
            false
        );
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // The "replace" sub-command is technically passed as an arg here.
        // This allows it to come after the initial (mayor) command, as the custom Towny command API
        // doesn't support adding sub-commands, only the main one.
        // Note that this shifts all args forwards one.

        // Ensure command is valid
        if (args.length < 3 || !args[0].equalsIgnoreCase("replace")) {
            sender.sendMessage(ChatColor.YELLOW + "/n mayor " + getName() + ChatColor.WHITE + " - " + getDescription());
            sender.sendMessage(ChatColor.GRAY + "Usage: " + getUsage());
            return true;
        }

        if (!isPlayer(sender)) {
            sendError(sender, "Only players can run this command.");
            return true;
        }

        if (!hasPermission(sender)) {   // This should not happen, as everyone should have permissions.
            sendError(sender, "You do not have permission to use this command.");
            return true;
        }

        String townName = args[1];
        String newMayorName = args[2];

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

        if (!senderNation.hasTown(town)) {
            sendError(sender, town.getName() + " is not a member of your nation.");
            return true;
        }

        // Disabled so any member in the nation can be in the new mayor.
        // TODO: This might cause issues if the mayor is the only resident of a town and they get removed and put into another town
//        if (!newMayor.hasTown() || !Objects.equals(newMayor.getTownOrNull(), town)) {
//            sendError(sender, newMayorName + " is not a citizen of " + town.getName() + ".");
//            return true;
//        }

        // Swap mayor
        Resident oldMayor = town.getMayor();
        try {
            town.setMayor(newMayor);

            TownyUniverse.getInstance().getDataSource().saveTown(town);
            TownyUniverse.getInstance().getDataSource().saveResident(newMayor);
            TownyUniverse.getInstance().getDataSource().saveNation(senderNation);
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
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        Resident senderResident = TownyAPI.getInstance().getResident(player.getUniqueId());
        if (senderResident == null) {
            return Collections.emptyList();
        }

        Nation senderNation;
        try {
            senderNation = senderResident.getNation();
        } catch (TownyException e) {
            return Collections.emptyList();
        }

        // See the first comment in the execute method for more details.
        if (args.length == 1) {
            return List.of("replace");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("replace")) {  // Suggest town
            String partial = args[1].toLowerCase();
            return senderNation.getTowns().stream()
                    .map(Town::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .limit(50)
                    .toList();
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("replace")) {  // Suggest new mayor
            String partial = args[2].toLowerCase();
            Town town = TownyAPI.getInstance().getTown(args[1]);
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
