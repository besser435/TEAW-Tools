package org.besser.teto.Commands.Towny.DeleteOldTowns;

import org.besser.teto.Commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class DeleteOldTownsCmd extends BaseCommand {
    public DeleteOldTownsCmd() {
        super("deletetownsinactive1year",
                "teto.towny.townPurge",
                "Delete towns who's entire population has not been online for at least one year.",
                "/teto deletetownsinactive1year [dryrun|run]",
                false,
                true
        );
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // Should never happen due to permissions, but this is a dangerous command so we need to check
        if (sender instanceof Player player && !player.isOp()) {
            sendError(sender, "Only operators can execute this command.");
            return true;
        }

        boolean dryRun = true;

        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "run" -> dryRun = false;
                case "dryrun" -> dryRun = true;
                default -> {
                    sendError(sender, "Invalid argument. Use 'dryrun' or 'run'.");
                    return true;
                }
            }
        }

        DeleteOldTowns.deleteOldTowns(dryRun);

        if (dryRun) {
            sendSuccess(sender, "Dry run complete. See console for details.");
        } else {
            sendSuccess(sender, "Town purge executed. Verify details in the console.");
        }

        return true;
    }

    @Override
    public List<String> customTabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            return Stream.of("dryrun", "run")
                    .filter(opt -> opt.startsWith(partial))
                    .toList();
        }

        return Collections.emptyList();
    }
}