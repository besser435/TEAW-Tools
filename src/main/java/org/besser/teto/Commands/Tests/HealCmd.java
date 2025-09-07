package org.besser.teto.Commands.Tests;

import org.besser.teto.Commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HealCmd extends BaseCommand {


    // TODO fix bug where there is no autocomplete for players on second arg (/teto heal <player> <-- HERE
    // Might be in abstract class.


    public HealCmd() {
        super("heal",
            "teto.heal",
            "Heal yourself or another player.",
            "/teto heal <player>",
            false,
            false
        );
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player target;

        if (args.length == 0) {
            if (!isPlayer(sender)) {
                sendError(sender, "Console must specify a player to heal.");
                return true;
            }
            target = getPlayer(sender);
        } else {
            target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sendError(sender, "Player '" + args[0] + "' not found.");
                return true;
            }
        }

        target.setHealth(Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getDefaultValue());
        target.setFoodLevel(20);
        target.setSaturation(20.0f);

        sendSuccess(sender, "Healed " + target.getName() + "!");
        if (!target.equals(sender)) {
            sendSuccess(target, "You have been healed!");
        }

        return true;
    }

    @Override
    public List<String> customTabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) { // Only complete the first arg
            String partial = args[0].toLowerCase();

            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .limit(50)
                    .toList();
        }
        return Collections.emptyList();
    }
}