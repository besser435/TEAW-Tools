package org.besser.teto.Commands;

import org.besser.teto.Commands.Tests.HealCmd;
import org.besser.teto.Commands.Towny.DeleteOldTowns.DeleteOldTownsCmd;
import org.besser.teto.Commands.Towny.MapColor.MapColorCmd;
import org.besser.teto.Commands.Towny.MayorReplaceCmd;
import org.besser.teto.Commands.Towny.NationOutlawCmd;
import org.besser.teto.Commands.Towny.TownyCommandAdapter;
import org.besser.teto.Teto;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.ChatColor;

import static org.besser.teto.DIETLogger.*;

import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final Teto plugin;
    private final HashMap<String, BaseCommand> commands = new HashMap<>();
    private final HashMap<String, PendingCommand> pendingConfirmations = new HashMap<>();

    private static class PendingCommand {
        final BaseCommand command;
        final String[] args;
        final long timestamp;

        PendingCommand(BaseCommand command, String[] args) {
            this.command = command;
            this.args = args;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public CommandManager(Teto plugin) {
        this.plugin = plugin;

        // Clean up expired confirmations every 60 seconds
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::cleanupExpiredConfirmations, 1200L, 1200L);
    }

    public void registerCommands() {
        // Register normal commands
        registerCommand(new HealCmd());
        registerCommand(new DeleteOldTownsCmd());

        // Register custom Towny extension commands
        TownyCommandAdapter.registerNationSubCommand("outlaw", new NationOutlawCmd());
        TownyCommandAdapter.registerNationSubCommand("mayor", new MayorReplaceCmd()); // TODO: add config option to disable
        TownyCommandAdapter.registerNationSubCommand("mapcolor", new MapColorCmd());

        // Register the main command executor
        Objects.requireNonNull(plugin.getCommand("teto")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("teto")).setTabCompleter(this);
    }

    public void registerCommand(BaseCommand command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        // Handle confirmation command
        if (args[0].equalsIgnoreCase("confirm")) {
            return handleConfirmation(sender);
        }

        // Handle cancel command
        if (args[0].equalsIgnoreCase("cancel")) {
            return handleCancellation(sender);
        }

        BaseCommand cmd = commands.get(args[0].toLowerCase());
        if (cmd == null) {
            sender.sendMessage( "[TETO] " + ChatColor.RED + "Unknown command. Use /teto for available commands.");
            showHelp(sender);
            return true;
        }

        // Check permissions
        if (!cmd.hasPermission(sender)) {
            sender.sendMessage("[TETO] " + ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        // Check if player only command
        if (cmd.isPlayerOnly() && !cmd.isPlayer(sender)) {
            sender.sendMessage("[TETO] This command can only be used by players.");
            return true;
        }

        String[] cmdArgs = Arrays.copyOfRange(args, 1, args.length);

        // Check if command requires confirmation
        if (cmd.requiresConfirmation()) {
            String senderKey = getSenderKey(sender);

            // Store the pending command
            pendingConfirmations.put(senderKey, new PendingCommand(cmd, cmdArgs));

            // Send confirmation message
            sender.sendMessage(ChatColor.YELLOW + "⚠ " + cmd.getConfirmationMessage(sender, cmdArgs));
            sender.sendMessage(ChatColor.GREEN + "Type " + ChatColor.WHITE + "/teto confirm" +
                               ChatColor.GREEN + " to proceed or " + ChatColor.WHITE + "/teto cancel" +
                               ChatColor.GREEN + " to cancel.");

            return true;
        }

        // Execute command with remaining arguments
        return executeCommand(cmd, sender, cmdArgs);
    }

    private boolean handleConfirmation(CommandSender sender) {
        String senderKey = getSenderKey(sender);
        PendingCommand pending = pendingConfirmations.remove(senderKey);

        if (pending == null) {
            sender.sendMessage(ChatColor.RED + "You have no pending command to confirm.");
            return true;
        }

        // Check if confirmation has expired (30 seconds)
        if (System.currentTimeMillis() - pending.timestamp > 30_000) {
            sender.sendMessage(ChatColor.RED + "Command confirmation has expired.");
            return true;
        }

        // Execute the confirmed command
        return executeCommand(pending.command, sender, pending.args);
    }

    private boolean handleCancellation(CommandSender sender) {
        String senderKey = getSenderKey(sender);
        PendingCommand pending = pendingConfirmations.remove(senderKey);

        if (pending == null) {
            sender.sendMessage(ChatColor.RED + "You have no pending command to cancel.");
            return true;
        }

        sender.sendMessage(ChatColor.BLUE + "Command cancelled.");
        return true;
    }

    private String getSenderKey(CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player) {
            return ((org.bukkit.entity.Player) sender).getUniqueId().toString();
        }
        return "CONSOLE";
    }

    private void cleanupExpiredConfirmations() {
        long currentTime = System.currentTimeMillis();
        pendingConfirmations.entrySet().removeIf(entry ->
                // 2 minutes, as to allow players to see the Expired warning. Otherwise, it gets deleted and they don't see that.
                currentTime - entry.getValue().timestamp > 120_000);
    }

    private boolean executeCommand(BaseCommand command, CommandSender sender, String[] args) {
        try {
            return command.execute(sender, args);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred while executing the command. Please notify an admin if this persists.");

            log(SEVERE, "[TETO] Error while executing command '" + command.getName() +
                        "' from sender " + sender.getName(), e);

            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> completions = new ArrayList<>();

            // Tab complete command names
            for (String cmdName : commands.keySet()) {
                BaseCommand cmd = commands.get(cmdName);
                if (cmd.hasPermission(sender) && cmdName.toLowerCase().startsWith(input)) {
                    completions.add(cmdName);
                }
            }

            // Always add confirm/cancel to tab completion
            if ("confirm".startsWith(input)) {
                completions.add("confirm");
            }
            if ("cancel".startsWith(input)) {
                completions.add("cancel");
            }

            return completions;
        }

        // Base command class completions
        BaseCommand cmd = commands.get(args[0].toLowerCase());
        if (cmd != null && cmd.hasPermission(sender)) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return cmd.customTabComplete(sender, alias, subArgs);
        }

        return Collections.emptyList();
    }

    // Does not show Towny commands fix later by adding them to the `commands` map (might break stuff so be careful).
    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Available Commands ===");
        for (BaseCommand cmd : commands.values()) {
            if (cmd.hasPermission(sender)) {
                sender.sendMessage(ChatColor.YELLOW + "/teto " + cmd.getName() +
                                   ChatColor.WHITE + " - " + cmd.getDescription());
                sender.sendMessage(ChatColor.GRAY + "Usage: " + cmd.getUsage());
            }
        }
    }
}