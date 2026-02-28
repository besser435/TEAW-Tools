package org.besser.teto.EnderPealNerf;

import org.besser.teto.Teto;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnderPearlCooldown implements Listener {
    // This is just a casting cooldown. If you have a pearl in an ender-porter for example,
    // you can still teleport, even when on a cooldown.

    private final Teto plugin;
    private final int cooldownTimeSeconds;

    public EnderPearlCooldown(Teto plugin) {
        this.plugin = plugin;
        this.cooldownTimeSeconds = plugin.getConfig().getInt("ender-pearl-nerf.cooldown-time-seconds", 180);
    }

    // Cancel if there is a cooldown
    @EventHandler
    public void onPearlInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getItem() == null || event.getItem().getType() != Material.ENDER_PEARL) return;

        if (player.hasCooldown(Material.ENDER_PEARL)) {
            event.setCancelled(true);

            double ticks = player.getCooldown(Material.ENDER_PEARL);
            double seconds = ticks / 20.0;

            player.sendMessage(ChatColor.RED + "Still on cooldown for another " +
                    String.format("%.1f", seconds) + " seconds!");
        }
    }

    // Apply cooldown after the throw (otherwise it will never let players throw it)
    @EventHandler
    public void onPearlLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl pearl) {
            if (pearl.getShooter() instanceof Player player) {
                // Run one tick later so Minecraft doesn't override our logic
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.setCooldown(Material.ENDER_PEARL, 20 * cooldownTimeSeconds);
                });
            }
        }
    }
}