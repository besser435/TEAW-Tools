package org.besser.teto.Commands.Towny.MapColor;


import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.event.TownBlockSettingsChangedEvent;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.event.town.TownMapColourNationalCalculationEvent;
import com.palmergames.bukkit.towny.event.town.toggle.TownToggleEvent;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.statusscreens.StatusScreen;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.besser.teto.DIETLogger.*;

public class TownColorChangeListener implements Listener {
    @EventHandler
    public void onTownColorChange(TownBlockSettingsChangedEvent event) {
        log(INFO, "TownBlockSettingsChangedEvent fired. Event name: " + event.getTownBlock().getName());

        TownyAPI towny = TownyAPI.getInstance();

        log(INFO, Objects.requireNonNull(towny.getTown("USA_Industries")).getMapColor().toString());
    }

    @EventHandler
    public void onMapColorCalc(TownMapColourNationalCalculationEvent event) {
        log(INFO, "TownMapColourNationalCalculationEvent fired. Event name: " + event.getEventName());

        log(INFO, "hex code: " + event.getMapColorHexCode());

        log(INFO, "TownySettings.getTownColorsMap()" + TownySettings.getTownColorsMap().values());
    }
}
