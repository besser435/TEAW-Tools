package org.besser.teto.Commands.Towny;

import org.besser.teto.Commands.BaseCommand;
import org.besser.teto.Teto;
import org.besser.teto.TownDecay.TownDecay;

import org.bukkit.command.CommandSender;

public class DecayTownsCmd extends BaseCommand {


    // TODO: Throws null pointer when decay is enabled. has to do with fucky logig in main Teto class.
    // Fix by creating a

    private final TownDecay townDecay;

    public DecayTownsCmd(Teto plugin) {
        super("decay",
            "teto.decay",
            "Ruins all inactive towns and sends an alert to towns that will become inactive soon.",
            "/teto decay",
            false);
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
}
