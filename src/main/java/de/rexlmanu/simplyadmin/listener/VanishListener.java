package de.rexlmanu.simplyadmin.listener;

import de.rexlmanu.simplyadmin.SimplyAdminPlugin;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class VanishListener implements Listener {
  private final SimplyAdminPlugin plugin;

  @EventHandler
  public void handlePlayerJoin(PlayerJoinEvent event) {
    // We need to hide all vanished players from the player who joined.
    this.plugin.getVanishedPlayers()
        .stream()
        .map(Bukkit::getPlayer)
        .filter(Objects::nonNull)
        .forEach(player -> event.getPlayer().hidePlayer(this.plugin, player));
  }

  @EventHandler
  public void handlePlayerQuit(PlayerQuitEvent event) {
    // We need to remove the player from the vanished players list.
    this.plugin.getVanishedPlayers().remove(event.getPlayer().getUniqueId());
  }

  @EventHandler
  public void handlePlayerDamage(EntityDamageEvent event) {
    // We need to cancel the event if the player is vanished.
    if (event.getEntity() instanceof Player player &&
        this.plugin.getVanishedPlayers().contains(player.getUniqueId())) {
      event.setCancelled(true);
    }
  }
}
