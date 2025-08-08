package org.besser.teto.TownDecay;

import com.palmergames.bukkit.towny.event.NewDayEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static org.besser.teto.DIETLogger.*;

public record TownDecayListener(TownDecay townDecay) implements Listener {

    // untested
    @EventHandler
    public void onNewDay(NewDayEvent event) {
        // only register this even in Teto.java if decay module is enabled
        log(INFO, "[Town decay] New Towny day, running town decay check");

        townDecay.runDecayCheckAndRuin();
    }
}
