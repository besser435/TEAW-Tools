package org.besser.teto.Commands;

import org.besser.teto.TownDecay.TownDecay;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

import static org.besser.teto.DIETLogger.*;

public class CommandHandler implements CommandExecutor, TabCompleter {
    private final TownDecay townDecay;

    // TODO: commands are also missing /teto: prefix

    public CommandHandler(TownDecay townDecay) {    // wtf is this, needs to not just be decay
        this.townDecay = townDecay;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        log(INFO, "Running command " + command.getName());

        townDecay.runDecayCheckAndRuin();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return List.of();
    }
}
