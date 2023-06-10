package net.simplyvanilla.simplyadmin.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.simplyvanilla.simplyadmin.SimplyAdminPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {
    private final SimplyAdminPlugin plugin;

    public FlyCommand(SimplyAdminPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target = null;

        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(this.plugin.getMessage("player-not-found",
                    Placeholder.unparsed("name", args[0])));
                return false;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        }

        if (target == null) {
            sender.sendMessage(this.plugin.getMessage("sender-has-to-be-a-player"));
            return false;
        }

        boolean allowFlight = target.getAllowFlight();
        target.setAllowFlight(!allowFlight);

        if (allowFlight) {
            target.sendMessage(plugin.getMessage("fly-disabled"));
        } else {
            target.sendMessage(plugin.getMessage("fly-enabled"));
        }

        return true;
    }
}
