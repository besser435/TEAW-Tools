package org.besser.teto.Commands.Towny.MapColor;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.besser.teto.Commands.Towny.TownyCommandAdapter;
import org.bukkit.command.CommandSender;
import com.palmergames.bukkit.towny.TownySettings;

import org.besser.teto.Commands.BaseCommand;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.besser.teto.DIETLogger.*;

public class MapColorCmd extends BaseCommand implements TownyCommandAdapter.TabCompletable {
    public MapColorCmd() {
        super("mapcolor",
                "teto.towny.nation.mapcolor",    // horrible
                "Sets a nation-wide map color for all towns, or locks town color changes to only the nation leader.",
                "/n mapcolor <color> || /n mapcolor lock",
                true,
                false
        );
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {

        /*
        Commands:
        /n mapcolor lock - prevents towns from changing their map color, only the nation leader can. Unsure of if
        there should be another toggle arg or if it should just flip the current state.
        Provide a warning when running this command that mayors won't be able to change their map colors
        so it would be advisable to run the following command:

        /n mapcolor <color>
        Sets every town's map color in the nation to the set color. Uses Map<String, String> allowedColors = TownySettings.getTownColorsMap();
        or maybe just hex codes.

        Handle the logic in separate methods, then just call the correct one from the execute method.

         */





        Player playerSender = (Player) sender;

        // TODO: make this more proper like the other commands. this gets the towns and residents in a weird way.
        Resident senderResident = TownyAPI.getInstance().getResident(playerSender);
        if (senderResident == null || !senderResident.hasTown()) {
            sender.sendMessage("You are not part of a town!");
            return true;
        }

        Town town;
        try {
            town = senderResident.getTown();
        } catch (Exception e) {
            sender.sendMessage("Error retrieving your town.");
            log(WARNING, e);
            return true;
        }


        // Get allowed colors from config
        Map<String, String> allowedColors = TownySettings.getTownColorsMap();
        String colorName = args[0].toLowerCase();

        if (!allowedColors.containsKey(colorName)) {
            playerSender.sendMessage("Invalid color! Allowed colors: " + String.join(", ", allowedColors.keySet()));
            return true;
        }

        // Set town map color
        town.setMapColorHexCode(allowedColors.get(colorName));
        playerSender.sendMessage("Town map color set to " + colorName);

        log(INFO, "Map color for town " + town.getName() + " set to " + allowedColors.get(colorName));
        return true;
    }










    @Override
    public List<String> customTownyTabComplete(CommandSender sender, String alias, String[] args) {
//        if (!(sender instanceof Player player)) {
//            return Collections.emptyList();
//        }
//
//        Resident senderResident = TownyAPI.getInstance().getResident(player.getUniqueId());
//        if (senderResident == null) {
//            return Collections.emptyList();
//        }
//
//        // See the first comment in the execute method for more details.
//        if (args.length == 1) {
//            return List.of("replace");
//        }
//
//        if (args.length == 2 && args[0].equalsIgnoreCase("replace")) {  // Suggest town
//            String partial = args[1].toLowerCase();
//            return senderNation.getTowns().stream()
//                    .map(Town::getName)
//                    .filter(name -> name.toLowerCase().startsWith(partial))
//                    .limit(50)
//                    .toList();
//        }
//
//        if (args.length == 3 && args[0].equalsIgnoreCase("replace")) {  // Suggest new mayor
//            String partial = args[2].toLowerCase();
//            Town town = TownyAPI.getInstance().getTown(args[1]);
//            if (town == null) return Collections.emptyList();
//
//            return town.getResidents().stream()
//                    .map(Resident::getName)
//                    .filter(name -> name.toLowerCase().startsWith(partial))
//                    .limit(50)
//                    .toList();
//        }
        return Collections.emptyList();
    }
}
