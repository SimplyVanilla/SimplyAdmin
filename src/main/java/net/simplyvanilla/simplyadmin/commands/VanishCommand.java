package net.simplyvanilla.simplyadmin.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.simplyvanilla.simplyadmin.SimplyAdminPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {
    private final SimplyAdminPlugin plugin;

    public VanishCommand(SimplyAdminPlugin plugin) {
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

        if (plugin.getVanishedPlayers().contains(target.getUniqueId())) {
            plugin.getVanishedPlayers().remove(target.getUniqueId());
            target.sendMessage(plugin.getMessage("vanish-disabled"));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(plugin, target);
            }
        } else {
            plugin.getVanishedPlayers().add(target.getUniqueId());
            target.sendMessage(plugin.getMessage("vanish-enabled"));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("simplyadmin.vanish.see")) {
                    onlinePlayer.hidePlayer(plugin, target);
                }
            }
        }

        return true;
    }
}
