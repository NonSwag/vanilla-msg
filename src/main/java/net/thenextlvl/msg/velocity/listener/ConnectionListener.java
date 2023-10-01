package net.thenextlvl.msg.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.msg.velocity.MessagePlugin;

import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
public class ConnectionListener {
    private final MessagePlugin plugin;

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        if (!event.getLoginStatus().equals(DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN)) return;
        new ArrayList<>(plugin.conversations().entrySet()).stream()
                .filter(entry -> entry.getValue().equals(event.getPlayer()))
                .map(Map.Entry::getKey)
                .forEach(plugin.conversations()::remove);
    }
}
