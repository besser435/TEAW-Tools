//package org.besser.teto.Commands.Towny.KickBannedPlayers;
//
//import com.palmergames.bukkit.towny.TownyUniverse;
//import com.palmergames.bukkit.towny.event.NewDayEvent;
//import com.palmergames.bukkit.towny.object.Resident;
//import com.palmergames.bukkit.towny.object.Town;
//import org.bukkit.BanEntry;
//import org.bukkit.BanList;
//import org.bukkit.Bukkit;
//import org.bukkit.OfflinePlayer;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import static org.besser.teto.DIETLogger.*;
//
//public class KickBannedPlayers implements Listener {
//
//    @EventHandler()
//    public void onTownyNewDayEvent(NewDayEvent event) {
//        kickBannedResidents();
//    }
//
//    public static void kickBannedResidents() {
//        List<Resident> residentsToKick = new ArrayList<>();
//        BanList<?> banList = Bukkit.getBanList(BanList.Type.PROFILE);
//
//        for (Resident resident : TownyUniverse.getInstance().getResidents()) {
//
//            if (!resident.hasTown()) {
//                continue;
//            }
//
//            UUID uuid = resident.getUUID();
//            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
//
//            if (offlinePlayer.isBanned()) {
//                // FIXME: deprecated method
//                BanEntry<?> banEntry = banList.getBanEntry(offlinePlayer.getName());
//
//                if (banEntry != null) {
//                    // Only kick if permanent
//                    if (banEntry.getExpiration() == null) {
//                        residentsToKick.add(resident);
//                    }
//                }
//            }
//        }
//
//        // Kick banned residents
//        if (residentsToKick.isEmpty()) {
//            log(INFO, "[Ban Purge] No permanently banned residents found to kick");
//            return;
//        }
//
//        for (Resident resident : residentsToKick) {
//            try {
//                Town town = resident.getTown();
//
//                //town.removeResident(resident);
//
//                // Save to ensure it persists
//                TownyUniverse.getInstance().getDataSource().saveTown(town);
//                TownyUniverse.getInstance().getDataSource().saveResident(resident);
//
//                log(INFO, "[Ban Purge] Kicked perm-banned resident " + resident.getName() + " from town " + town.getName());
//
//            } catch (Exception e) {
//                log(WARNING, "[Ban Purge] Error kicking " + resident.getName() + ": " +  e.getMessage());
//            }
//        }
//    }
//}
