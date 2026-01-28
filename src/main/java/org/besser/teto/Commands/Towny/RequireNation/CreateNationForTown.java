package org.besser.teto.Commands.Towny.RequireNation;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.TownySettings;

import java.util.UUID;

import static org.besser.teto.DIETLogger.*;

public class CreateNationForTown {
    private static final int maxLimit = TownySettings.getMaxNameLength();

    public static void giveNation(Town town) {
        try {
            // Safety Checks
            if (town == null || town.hasNation()) return;

            Resident mayor = town.getMayor();
            if (mayor == null) {
                return;
            }

            TownyUniverse universe = TownyUniverse.getInstance();

            // Create nation
            String nationName = generateNationName(town, universe);
            Nation nation = new Nation(nationName);

            nation.setUUID(UUID.randomUUID());  // If this is not here you get shitfuck issues

            nation.setCapital(town);
            town.setNation(nation); // Doing the opposite of nation.addTown(town); creates bugs for some reason
            universe.registerNation(nation);

            // Prevent infinite money glitch. On TEAW, all new nations start with $10,000.
            if (TownySettings.isUsingEconomy() && nation.getAccount() != null) {
                nation.getAccount().setBalance(0, "Resetting initial nation bank balance to zero.");
            }

            universe.getDataSource().saveNation(nation);
            universe.getDataSource().saveTown(town);

            log(INFO, "Created nation " + nation.getName() +
                    " for town " + town.getName() +
                    " with leader " + nation.getKing().toString());

        } catch (AlreadyRegisteredException e) {
            log(WARNING, "Nation already exists for town " + town.getName(), e);
        } catch (Exception e) {
            log(SEVERE,"Failed to create nation for town " + town.getName(), e);
        }
    }

    /**
     * Find nation name that fits the character limit.
     * Prioritizes Prefix + Town, then Town name alone, then Truncated Town name.
     * Ensures no duplicate nation names.
     */
    private static String generateNationName(Town town, TownyUniverse universe) {
        String name = town.getName().replace(" ", "_"); // Just in case, or for future modifications

        // Trim if the town name itself exceeds Towny's limits
        if (name.length() > maxLimit) {
            name = name.substring(0, maxLimit);
        }

        // Handle duplicates by appending a short UUID suffix
        if (universe.hasNation(name)) {
            String suffix = "_" + UUID.randomUUID().toString().substring(0, 4);

            // If adding the suffix pushes it over the limit, trim the name further
            if (name.length() + suffix.length() > maxLimit) {
                int trimIndex = Math.max(0, maxLimit - suffix.length());
                name = name.substring(0, trimIndex) + suffix;
            } else {
                name += suffix;
            }
        }

        return name;
    }
}
