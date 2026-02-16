package org.besser.teto.Commands.Towny.MapColor;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import org.besser.teto.Commands.BaseCommand;
import org.besser.teto.Commands.Towny.TownyCommandAdapter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.besser.teto.DIETLogger.*;

public class MapColorCmd extends BaseCommand implements TownyCommandAdapter.TabCompletable {
    public MapColorCmd() {
        super("mapcolor",
                "teto.towny.nation.mapcolor",
                "Sets a nation-wide web-map color for all towns, or locks town color changes so only the nation leader can change them.",
                "/n mapcolor set <color> or /n mapcolor lock",
                true,
                false
        );
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // Ensure valid command usage
        if (args.length < 1) {
            sender.sendMessage(ChatColor.YELLOW + "/n " + getName() + ChatColor.WHITE + " - " + getDescription());
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

        // Run sender checks
        // Fix duplicated code fragment. Make helper class?
        Player playerSender = (Player) sender;
        Resident senderResident = TownyAPI.getInstance().getResident(playerSender);

        if (senderResident == null) {
            return true;
        }

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

        // Determine subcommand and call proper method
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "lock" -> handleLockCommand(sender, senderNation);
            case "set" -> {
                if (args.length < 2) {  // Prevent towny out of bounds error
                    sendError(sender, "Usage: /n mapcolor set <color>");
                    return true;
                }
                handleSetColorCommand(sender, senderNation, args[1]);
            }
            default -> {
                sender.sendMessage(ChatColor.YELLOW + "/n " + getName() + ChatColor.WHITE + " - " + getDescription());
                sender.sendMessage(ChatColor.GRAY + "Usage: " + getUsage());
            }
        }

        return true;
    }

    private void handleLockCommand(CommandSender sender, Nation nation) {
        final String MAPCOLOR_LOCK_KEY = "teto_mapcolor_locked";

        BooleanDataField lockField = nation.getMetadata(MAPCOLOR_LOCK_KEY, BooleanDataField.class);

        if (lockField == null) {
            lockField = new BooleanDataField(MAPCOLOR_LOCK_KEY, false);
            nation.addMetaData(lockField);
        }

        // Flip value
        boolean currentlyLocked = lockField.getValue();
        boolean nowLocked = !currentlyLocked;
        lockField.setValue(nowLocked);
        nation.save();

        // Send message
        String message = nowLocked
                ? "Town map color changes are now" + ChatColor.RED + " locked."
                + ChatColor.GREEN + " Only the nation leader can change them."

                : "Town map color changes are now" + ChatColor.DARK_GREEN + " unlocked."
                + ChatColor.GREEN + " Mayors can now freely change their map colors.";
        sendSuccess(sender, message);
    }

    private void handleSetColorCommand(CommandSender sender, Nation nation, String colorArg) {
        Map<String, String> allowedColors = TownySettings.getTownColorsMap();

        if (!allowedColors.containsKey(colorArg.toLowerCase())) {
            // TODO allow custom hex codes.
            // Kind of a lot of options, maybe dont include them and just rely on tab complete.
            sendError(sender, "Invalid color! Allowed colors: " + String.join(", ", allowedColors.keySet()));
            return;
        }

        String hex = allowedColors.get(colorArg.toLowerCase());

        // Apply color to all towns in the nation
        for (Town town : nation.getTowns()) {
            try {
                town.setMapColorHexCode(hex);
            } catch (Exception e) {
                log(WARNING, "Failed to set color for town " + town.getName() + ": " + e.getMessage());
            }
        }

        TownyUniverse.getInstance().getDataSource().saveAll();

        sendSuccess(sender, "Map color set to " + colorArg + " for all towns in the nation.");
    }


    @Override
    public List<String> customTownyTabComplete(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player player)) {   // Just in case the console gets here
            return Collections.emptyList();
        }

        Resident senderResident = TownyAPI.getInstance().getResident(player.getUniqueId());
        if (senderResident == null) {
            return Collections.emptyList();
        }

        // set || lock
        if (args.length == 1) {
            return Stream.of("set", "lock")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        // color
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return TownySettings.getTownColorsMap()
                    .keySet()
                    .stream()
                    .filter(color -> color.startsWith(args[1].toLowerCase()))
                    .sorted()
                    .toList();
        }

        return Collections.emptyList();
    }
}
