package net.thenextlvl.msg.paper.listener;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.msg.paper.MessagePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
public class ConnectionListener implements Listener {
    private final MessagePlugin plugin;

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        new ArrayList<>(plugin.conversations().entrySet()).stream()
                .filter(entry -> entry.getValue().equals(event.getPlayer()))
                .map(Map.Entry::getKey)
                .forEach(plugin.conversations()::remove);
    }
}
