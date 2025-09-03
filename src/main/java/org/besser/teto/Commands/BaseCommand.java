package org.besser.teto.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public abstract class BaseCommand {
    protected final String name;
    protected final String permission;
    protected final String description;
    protected final String usage;
    protected final boolean playerOnly;

    public BaseCommand(String name, String permission, String description, String usage, boolean playerOnly) {
        this.name = name;
        this.permission = permission;
        this.description = description;
        this.usage = usage;
        this.playerOnly = playerOnly;
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    public String getName() { return name; }
    public String getPermission() { return permission; }
    public String getDescription() { return description; }
    public String getUsage() { return usage; }
    public boolean isPlayerOnly() { return playerOnly; }


    // TODO: adding a confirmation feature for commands that really really should be double checked seems hard. They need
    // tickets, and ya gotta keep track of it all. Fix this by just making any commands that need confirmation end with
    // "confirm-destructive" argument or something.

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

    protected boolean isPlayer(CommandSender sender) {  // TODO: IntelliJ is complaining, real?
        return sender instanceof Player;
    }

    protected Player getPlayer(CommandSender sender) {
        return (Player) sender;
    }
}