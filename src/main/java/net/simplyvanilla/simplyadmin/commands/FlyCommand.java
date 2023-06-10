package net.simplyvanilla.simplyadmin.commands;

import net.simplyvanilla.simplyadmin.SimplyAdminPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends PlayerToggleCommand implements CommandExecutor {

    public FlyCommand(SimplyAdminPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        togglePlayer(args, sender, new ToggleFunction() {
            @Override
            public boolean isToggled(Player player) {
                return player.getAllowFlight();
            }

            @Override
            public void enable(Player player) {
                player.setAllowFlight(true);
            }

            @Override
            public void disable(Player player) {
                player.setAllowFlight(false);
            }
        }, "fly-enabled", "fly-disabled");
        return true;
    }
}
