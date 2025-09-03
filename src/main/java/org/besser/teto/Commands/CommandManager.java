package org.besser.teto.Commands;

import org.besser.teto.Commands.Tests.HealCmd;
import org.besser.teto.Commands.Towny.DecayTownsCmd;
import org.besser.teto.Teto;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.ChatColor;

import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final Teto plugin;
    private final HashMap<String, BaseCommand> commands = new HashMap<>();

    public CommandManager(Teto plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {

        // Check if commands are enabled and register them
        registerCommand(new HealCmd());
        if (plugin.getTownDecay() != null) registerCommand(new DecayTownsCmd(plugin));
        //registerCommand(new NationOutlawCmd());

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

        BaseCommand cmd = commands.get(args[0].toLowerCase());
        if (cmd == null) {
            sender.sendMessage( "[TETO] " + ChatColor.RED + "Unknown command. Use /teto help for available commands.");
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

        // Execute command with remaining arguments
        String[] cmdArgs = Arrays.copyOfRange(args, 1, args.length);
        try {
            return cmd.execute(sender, cmdArgs);
        } catch (Exception e) {
            sender.sendMessage("[TETO] " + ChatColor.RED + "An error occurred while executing the command.");
            e.printStackTrace();    // TODO: handle this better before moving into prod
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Tab complete command names
            for (String cmdName : commands.keySet()) {
                BaseCommand cmd = commands.get(cmdName);
                if (cmd.hasPermission(sender) && cmdName.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(cmdName);
                }
            }
        }

        return completions;
    }

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