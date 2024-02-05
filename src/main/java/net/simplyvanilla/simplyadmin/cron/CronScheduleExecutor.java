package net.simplyvanilla.simplyadmin.cron;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.simplyvanilla.simplyadmin.SimplyAdminPlugin;
import org.bukkit.configuration.ConfigurationSection;

public class CronScheduleExecutor {
    record CronEntry(int interval, List<String> commands) {
    }

    private final SimplyAdminPlugin plugin;

    private final Map<CronEntry, String> lastExecutedCommand = new HashMap<>();
    private final Set<Integer> taskIds = new HashSet<>();

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
        int taskId =
            this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
                String lastCommand = this.lastExecutedCommand.get(entry);
                for (String command : entry.commands) {
                    if (command.equals(lastCommand)) {
                        continue;
                    }
                    this.plugin.getServer()
                        .dispatchCommand(this.plugin.getServer().getConsoleSender(), command);
                    this.lastExecutedCommand.put(entry, command);
                    return;
                }
            }, 0L, entry.interval * 20L);

        this.taskIds.add(taskId);
    }

    public void cancel() {
        for (int taskId : this.taskIds) {
            this.plugin.getServer().getScheduler().cancelTask(taskId);
        }
    }
}
