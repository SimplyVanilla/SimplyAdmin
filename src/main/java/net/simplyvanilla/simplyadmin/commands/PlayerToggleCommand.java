package net.simplyvanilla.simplyadmin.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.simplyvanilla.simplyadmin.SimplyAdminPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerToggleCommand {

    protected final SimplyAdminPlugin plugin;

    private PlayerToggleCommand(SimplyAdminPlugin plugin) {
        this.plugin = plugin;
    }

    protected void togglePlayer(String[] args, CommandSender sender, ToggleFunction toggleFunction,
                                String enabledMessageKey, String disabledMessageKey) {
        Player target = null;

        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(this.plugin.getMessage("player-not-found",
                    Placeholder.unparsed("name", args[0])));
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        }

        if (target == null) {
            sender.sendMessage(this.plugin.getMessage("sender-has-to-be-a-player"));
            return;
        }

        boolean isCurrentlyToggled = toggleFunction.isToggled(target);

        if (isCurrentlyToggled) {
            toggleFunction.disable(target);
            target.sendMessage(plugin.getMessage(disabledMessageKey));
        } else {
            toggleFunction.enable(target);
            target.sendMessage(plugin.getMessage(enabledMessageKey));
        }
    }

    protected interface ToggleFunction {
        boolean isToggled(Player player);

        void enable(Player player);

        void disable(Player player);
    }
}
