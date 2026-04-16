package org.besser.teto.ItemTweaks;

import org.besser.teto.Teto;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ItemCooldowns implements Listener {
    private final Teto plugin;
    private final int enderPearlCooldownTicks;
    private final int enchantedAppleCooldownTicks;

    public ItemCooldowns(Teto plugin) {
        this.plugin = plugin;
        this.enderPearlCooldownTicks = plugin.getConfig().getInt("item-cooldowns.ender-pearl-cooldown-time-seconds", 40) * 20;
        this.enchantedAppleCooldownTicks = plugin.getConfig().getInt("item-cooldowns.e-apple-cooldown-time-seconds", 300) * 20;
    }

    // Cancel if there is a cooldown
    // This is just a casting cooldown. If you have a pearl in an ender-porter for example,
    // you can still teleport, even when on a cooldown.
    @EventHandler
    public void onPearlInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getItem() == null || event.getItem().getType() != Material.ENDER_PEARL) return;

        if (player.hasCooldown(Material.ENDER_PEARL)) {
            event.setCancelled(true);
        }
    }

    // Ender Pearl Cooldown
    // Apply cooldown after the throw (otherwise it will never let players throw it)
    @EventHandler
    public void onPearlLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl pearl) {
            if (pearl.getShooter() instanceof Player player) {
                // Run one tick later so Minecraft doesn't override our logic
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.setCooldown(Material.ENDER_PEARL, enderPearlCooldownTicks);
                });
            }
        }
    }

    // Enchanted Golden Apple Cooldown
    // TODO: Should be more generic. Just specify item ID in a list with seconds.
    // Would need to check it doesnt have custom cooldown like ender pearl, because that has to be handled manually.
    @EventHandler
    public void onAppleConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            Player player = event.getPlayer();
            player.setCooldown(Material.ENCHANTED_GOLDEN_APPLE, enchantedAppleCooldownTicks);
        }

        // Won't do anything to E-Apples fired from tater guns
        // suboptimal
    }
}