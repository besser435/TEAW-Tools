package org.besser.teto.Commands.Towny;

import org.besser.teto.Commands.BaseCommand;
import org.besser.teto.Teto;
import org.besser.teto.TownDecay.TownDecay;

import org.bukkit.command.CommandSender;

public class DecayTownsCmd extends BaseCommand {
    private final TownDecay townDecay;

    public DecayTownsCmd(Teto plugin) {
        super("decay",
            "teto.decay",
            "Ruins all inactive towns and sends an alert to towns that will become inactive soon.",
            "/teto decay",
            false,
            true);
        this.townDecay = plugin.getTownDecay();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // The check if decay is enabled is done in CommandManager

        // TODO: Add confirmation, this is a destructive action!
        int decayedTowns = townDecay.runDecayCheckAndRuin();
        sendSuccess(sender,"Ruined " + decayedTowns + " towns!");

        return true;
    }

    @Override
    public String getConfirmationMessage(CommandSender sender, String[] args) {
        return "This will put towns into the ruined state. Are you sure you want to execute this command?";
    }
}
