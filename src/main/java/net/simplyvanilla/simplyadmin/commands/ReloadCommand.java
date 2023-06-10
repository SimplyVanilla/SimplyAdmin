package net.simplyvanilla.simplyadmin.commands;

import net.simplyvanilla.simplyadmin.SimplyAdminPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final SimplyAdminPlugin plugin;

    public ReloadCommand(SimplyAdminPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadConfig();
        sender.sendMessage(plugin.getMessage("reload"));
        return true;
    }
}
