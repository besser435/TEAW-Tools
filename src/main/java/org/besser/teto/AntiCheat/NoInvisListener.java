package org.besser.teto.AntiCheat;

import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

public class NoInvisListener implements Listener {
    @EventHandler
    public void onPotionEffectAdd(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Check if an effect is actually being added or changed.
        // If it's REMOVED or CLEARED, getNewEffect() will be null.
        if (event.getAction() != EntityPotionEffectEvent.Action.ADDED &&
                event.getAction() != EntityPotionEffectEvent.Action.CHANGED) {
            return;
        }

        if (event.getNewEffect() != null &&
                event.getNewEffect().getType().equals(PotionEffectType.INVISIBILITY)) {

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "TEAW Anti-cheat: Invisibility is disabled!");
            player.playNote(player.getLocation(), Instrument.DIDGERIDOO, Note.natural(1, Note.Tone.G));
        }
    }
}
