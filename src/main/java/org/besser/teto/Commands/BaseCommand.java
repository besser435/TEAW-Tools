package org.besser.teto.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.Collections;
import java.util.List;

public abstract class BaseCommand {
    protected final String name;
    protected final String permission;
    protected final String description;
    protected final String usage;
    protected final boolean playerOnly;
    protected final boolean requiresConfirmation;

    public BaseCommand(String name, String permission, String description, String usage, boolean playerOnly, boolean requiresConfirmation) {
        this.name = name;
        this.permission = permission;
        this.description = description;
        this.usage = usage;
        this.playerOnly = playerOnly;
        this.requiresConfirmation = requiresConfirmation;
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    public List<String> customTabComplete(CommandSender sender, String alias, String[] args) {return Collections.emptyList();}

    public String getName() { return name; }
    public String getPermission() { return permission; }
    public String getDescription() { return description; }
    public String getUsage() { return usage; }
    public boolean isPlayerOnly() { return playerOnly; }
    public boolean requiresConfirmation() { return requiresConfirmation; }

    protected boolean hasPermission(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
    }

    protected void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    protected void sendError(CommandSender sender, String error) {
        sender.sendMessage(ChatColor.RED + error);
    }

    protected void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GREEN + message);
    }

    protected boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    protected Player getPlayer(CommandSender sender) {
        return (Player) sender;
    }

    protected String getConfirmationMessage(CommandSender sender, String[] args) {
        return "Are you sure you want to execute this command?";
    }
}