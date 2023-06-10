package net.simplyvanilla.simplyadmin.commands;

import net.simplyvanilla.simplyadmin.SimplyAdminPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends PlayerToggleCommand implements CommandExecutor {
    public VanishCommand(SimplyAdminPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        togglePlayer(args, sender, new ToggleFunction() {
            @Override
            public boolean isToggled(Player player) {
                return plugin.getVanishedPlayers().contains(player.getUniqueId());
            }

            @Override
            public void enable(Player player) {
                plugin.getVanishedPlayers().add(player.getUniqueId());
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayer.canSee(player) || onlinePlayer.hasPermission("simplyadmin.vanish.see")) {
                        continue;
                    }
                    onlinePlayer.hidePlayer(plugin, player);
                }
            }

            @Override
            public void disable(Player player) {
                plugin.getVanishedPlayers().remove(player.getUniqueId());
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.canSee(player)) {
                        continue;
                    }
                    onlinePlayer.showPlayer(plugin, player);
                }
            }
        }, "vanish-enabled", "vanish-disabled");
        return true;
    }
}
