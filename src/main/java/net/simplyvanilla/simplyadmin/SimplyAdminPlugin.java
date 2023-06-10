package net.simplyvanilla.simplyadmin;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.simplyvanilla.simplyadmin.listener.VanishListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class SimplyAdminPlugin extends JavaPlugin {

    @Getter
    private final List<UUID> vanishedPlayers = new ArrayList<>();
    private CommandManager<CommandSender> commandManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.setupCommandManager();
        this.registerCommands();

        // Register the vanish listener
        Bukkit.getPluginManager().registerEvents(new VanishListener(this), this);
    }

    private void setupCommandManager() {
        try {
            this.commandManager = new PaperCommandManager<>(
                this,
                AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build(),
                Function.identity(),
                Function.identity()
            );
        } catch (Exception e) {
            this.getLogger().severe("Failed to initialize command manager, please use Paper.");
            this.getLogger().severe("Disabling plugin...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    private void registerCommands() {
        // fly command
        this.commandManager.command(this.commandManager.commandBuilder("fly")
            .senderType(Player.class)
            .permission("simplyadmin.fly")
            .handler(commandContext -> {
                Player player = (Player) commandContext.getSender();

                boolean allowFlight = player.getAllowFlight();
                player.setAllowFlight(!allowFlight);

                if (allowFlight) {
                    player.sendMessage(this.getMessage("fly-disabled"));
                } else {
                    player.sendMessage(this.getMessage("fly-enabled"));
                }
            }));

        // fly other command
        this.commandManager.command(this.commandManager.commandBuilder("fly")
            .argument(PlayerArgument.of("target"))
            .permission("simplyadmin.fly.other")
            .handler(commandContext -> {
                Player target = commandContext.get("target");

                boolean allowFlight = target.getAllowFlight();
                target.setAllowFlight(!allowFlight);

                if (allowFlight) {
                    commandContext.getSender().sendMessage(this.getMessage("fly-disabled-other",
                        Placeholder.component("name", target.displayName())));
                } else {
                    commandContext.getSender().sendMessage(this.getMessage("fly-enabled-other",
                        Placeholder.component("name", target.displayName())));
                }
            }));

        // vanish command
        this.commandManager.command(this.commandManager.commandBuilder("vanish")
            .senderType(Player.class)
            .permission("simplyadmin.vanish")
            .handler(commandContext -> {
                Player player = (Player) commandContext.getSender();

                // Check if player is already vanished
                if (this.vanishedPlayers.contains(player.getUniqueId())) {
                    this.vanishedPlayers.remove(player.getUniqueId());
                    player.sendMessage(this.getMessage("vanish-disabled"));
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.canSee(player)) {
                            continue;
                        }
                        onlinePlayer.showPlayer(this, player);
                    }
                    return;
                }

                this.vanishedPlayers.add(player.getUniqueId());
                player.sendMessage(this.getMessage("vanish-enabled"));

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayer.canSee(player)) {
                        continue;
                    }
                    // Check if player has permission to see vanished players
                    if (onlinePlayer.hasPermission("simplyadmin.vanish.see")) {
                        continue;
                    }
                    onlinePlayer.hidePlayer(this, player);
                }
            }));

        // vanish other command
        this.commandManager.command(this.commandManager.commandBuilder("vanish")
            .argument(PlayerArgument.of("target"))
            .permission("simplyadmin.vanish.other")
            .handler(commandContext -> {
                Player target = commandContext.get("target");

                // Check if player is already vanished
                if (this.vanishedPlayers.contains(target.getUniqueId())) {
                    this.vanishedPlayers.remove(target.getUniqueId());
                    commandContext.getSender().sendMessage(this.getMessage("vanish-disabled-other",
                        Placeholder.component("name", target.displayName())));

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.canSee(target)) {
                            continue;
                        }
                        onlinePlayer.showPlayer(this, target);
                    }
                    return;
                }

                this.vanishedPlayers.add(target.getUniqueId());
                commandContext.getSender().sendMessage(this.getMessage("vanish-enabled-other",
                    Placeholder.component("name", target.displayName())));

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayer.canSee(target)) {
                        continue;
                    }
                    // Check if player has permission to see vanished players
                    if (onlinePlayer.hasPermission("simplyadmin.vanish.see")) {
                        continue;
                    }
                    onlinePlayer.hidePlayer(this, target);
                }
            }));

        // reload command
        this.commandManager.command(this.commandManager.commandBuilder("simplyadmin")
            .literal("reload")
            .permission("simplyadmin.reload")
            .handler(commandContext -> {
                this.reloadConfig();
                commandContext.getSender().sendMessage(this.getMessage("reload"));
            }));
    }

    private Component getMessage(String key, TagResolver... tagResolvers) {
        String string = this.getConfig().getString("messages." + key);
        if (string == null) {
            return Component.empty();
        }
        return MiniMessage.miniMessage().deserialize(string, tagResolvers);
    }
}
