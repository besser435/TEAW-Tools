package org.besser.teto.Commands.Towny.MapColor;

import org.besser.teto.Commands.Towny.TownyCommandAdapter;
import org.bukkit.command.CommandSender;

import org.besser.teto.Commands.BaseCommand;

import java.util.Collections;
import java.util.List;

public class MapColorCmd extends BaseCommand implements TownyCommandAdapter.TabCompletable {
    public MapColorCmd() {
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
