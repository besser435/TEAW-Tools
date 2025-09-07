package org.besser.teto.Commands.Towny;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import org.besser.teto.Commands.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

import static org.besser.teto.DIETLogger.log;

/*
 TETO uses the /teto prefix. But if we want to add onto Towny, we don't want that prefix.
 Towny has an API to let you register our own commands. It expects a commandExecutor, but we have our own custom one.
 This adapter lets us register custom Towny commands with its own commandExecuter. It just wraps baseCommand in an executer.
 */

public class TownyCommandAdapter implements CommandExecutor, TabCompleter {
    private final BaseCommand baseCommand;

    public TownyCommandAdapter(BaseCommand baseCommand) {
        this.baseCommand = baseCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return baseCommand.execute(sender, args);
    }

    public static void registerNationSubCommand(String name, BaseCommand cmd) {
        TownyCommandAddonAPI.addSubCommand(TownyCommandAddonAPI.CommandType.NATION, name, new TownyCommandAdapter(cmd));
    }

    // Small interface so only commands that care implement tab completion
    public interface TabCompletable {
        List<String> customTownyTabComplete(CommandSender sender, String alias, String[] args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Only works if BaseCommand overrides tabComplete
        if (baseCommand instanceof TabCompletable completable) {
            return completable.customTownyTabComplete(sender, alias, args);
        }
        return Collections.emptyList();
    }
}
