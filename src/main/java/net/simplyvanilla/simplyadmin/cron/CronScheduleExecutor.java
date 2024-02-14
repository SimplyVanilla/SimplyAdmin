package net.simplyvanilla.simplyadmin.cron;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.simplyvanilla.simplyadmin.SimplyAdminPlugin;
import org.bukkit.configuration.ConfigurationSection;

public class CronScheduleExecutor {
    record CronEntry(int interval, List<String> commands) {
    }

    private final SimplyAdminPlugin plugin;

    private final Map<CronEntry, Integer> currentCommandIndex = new ConcurrentHashMap<>();

    public CronScheduleExecutor(SimplyAdminPlugin plugin, ConfigurationSection section) {
        this.plugin = plugin;

        this.parseEntries(section);
    }

    private void parseEntries(ConfigurationSection section) {
        List<Map<?, ?>> cronsList = section.getMapList("crons");
        for (Map<?, ?> values : cronsList) {
            if (!values.containsKey("interval")) {
                continue;
            }
            int interval = (int) values.get("interval");
            List<String> commands = (List<String>) values.get("commands");
            CronEntry entry = new CronEntry(interval, commands);
            this.schedule(entry);
        }
    }

    private void schedule(CronEntry entry) {
        this.plugin.getServer().getAsyncScheduler()
            .runAtFixedRate(this.plugin, scheduledTask -> {
                int currentIndex = this.currentCommandIndex.getOrDefault(entry, 0);
                if (currentIndex >= entry.commands.size()) {
                    currentIndex = 0;
                }

                String command = entry.commands.get(currentIndex);
                this.plugin.getServer()
                    .dispatchCommand(this.plugin.getServer().getConsoleSender(), command);
                this.currentCommandIndex.put(entry, currentIndex + 1);
            }, 0, entry.interval, TimeUnit.SECONDS);
    }
}
