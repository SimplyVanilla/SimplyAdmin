package net.simplyvanilla.simplyadmin.listener;

import java.util.List;
import net.simplyvanilla.simplyadmin.SimplyAdminPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NewPlayerListener implements Listener {
    private final SimplyAdminPlugin plugin;
    private final List<String> commands;

    public NewPlayerListener(SimplyAdminPlugin plugin) {
        this.plugin = plugin;

        this.commands = plugin.getConfig().getStringList("events.new-joiner");
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPlayedBefore()) {
            return;
        }

        for (String command : this.commands) {
            this.plugin.getServer().dispatchCommand(
                this.plugin.getServer().getConsoleSender(),
                command.replace("%player%", event.getPlayer().getName())
            );
        }
    }
}
