package net.thenextlvl.msg.paper;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class MessagePlugin extends JavaPlugin {
    private final Metrics metrics = new Metrics(this, 19942);

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        metrics.shutdown();
    }
}
