package org.besser.teto.Commands.Towny.DeleteOldTowns;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.besser.teto.DIETLogger.*;

public class DeleteOldTowns {
    public static void deleteOldTowns(boolean dryRun) {
        long oneYearAgo = System.currentTimeMillis() - (365L * 24 * 60 * 60 * 1000);
        List<Town> townsToDelete = new ArrayList<>();

//        int counter = 0;
//        for (Town town : TownyUniverse.getInstance().getTowns()) {
//            for (Resident resident : town.getResidents()) {
//                UUID uuid = resident.getUUID();
//
//                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
//                long lastPlayed = offlinePlayer.getLastPlayed();
//
//                if (offlinePlayer.isOnline() || lastPlayed <= 1 || lastPlayed > oneYearAgo) {
//                    continue;  // Active
//                }
//
//                counter++;
//
//                log(INFO, "Would kick " + resident.getName() + " from " + town.getName());
//            }
//        }
//        log(INFO, "Would kick " + counter + " residents");


        for (Town town : TownyUniverse.getInstance().getTowns()) {
            boolean hasActiveResident = false;

            for (Resident resident : town.getResidents()) {
                UUID uuid = resident.getUUID();

                if (uuid == null) {
                    hasActiveResident = true;
                    break;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                long lastPlayed = offlinePlayer.getLastPlayed();

                if (offlinePlayer.isOnline() || lastPlayed <= 1 || lastPlayed > oneYearAgo) {
                    hasActiveResident = true;
                    break;
                }

                if (town.getName().equals("Cool_Neighbourhood_of_Croatia")) hasActiveResident = true;   // :3
            }

            if (!hasActiveResident) {
                townsToDelete.add(town);
            }
        }

        // Delete towns
        if (townsToDelete.isEmpty()) {
            log(INFO, "[Town Purge] No inactive towns found to delete");
            return;
        }

        for (Town town : townsToDelete) {
            try {
                if (!dryRun) {
                    TownyUniverse.getInstance().getDataSource().removeTown(town, DeleteTownEvent.Cause.ADMIN_COMMAND);
                }

                log(INFO, "[Town Purge] deleted town: " + town.getName() + " from " + town.getNation().getName());
            } catch (Exception e) {
                log(WARNING, "[Town Purge] Failed to delete town " + town.getName() + ": " + e.getMessage());
            }
        }

        TownyUniverse.getInstance().getDataSource().saveAll();

        log(INFO, "[Town Purge] Purged " + townsToDelete.size() + " towns");

        if (dryRun) log(INFO, "[Town Purge] This was a dry run. These towns were not deleted.");
    }
}
