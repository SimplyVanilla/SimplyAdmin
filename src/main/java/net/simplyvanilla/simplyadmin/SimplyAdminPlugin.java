package net.simplyvanilla.simplyadmin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.simplyvanilla.simplyadmin.commands.FlyCommand;
import net.simplyvanilla.simplyadmin.commands.ReloadCommand;
import net.simplyvanilla.simplyadmin.commands.VanishCommand;
import net.simplyvanilla.simplyadmin.cron.CronScheduleExecutor;
import net.simplyvanilla.simplyadmin.listener.NewPlayerListener;
import net.simplyvanilla.simplyadmin.listener.VanishListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimplyAdminPlugin extends JavaPlugin {

    @Getter
    private final List<UUID> vanishedPlayers = new ArrayList<>();

    private CronScheduleExecutor cronScheduleExecutor;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.cronScheduleExecutor = new CronScheduleExecutor(this, this.getConfig());

        this.getCommand("fly").setExecutor(new FlyCommand(this));
        this.getCommand("vanish").setExecutor(new VanishCommand(this));
        this.getCommand("simplyadmin").setExecutor(new ReloadCommand(this));

        // Register the vanish listener
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new VanishListener(this), this);
        manager.registerEvents(new NewPlayerListener(this), this);
    }

    public Component getMessage(String key, TagResolver... tagResolvers) {
        String string = this.getConfig().getString("messages." + key);
        if (string == null) {
            return Component.empty();
        }
        return MiniMessage.miniMessage().deserialize(string, tagResolvers);
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
