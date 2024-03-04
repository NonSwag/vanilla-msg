package net.thenextlvl.msg.paper;

import core.i18n.file.ComponentBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.msg.paper.command.MessageCommand;
import net.thenextlvl.msg.paper.command.ReplyCommand;
import net.thenextlvl.msg.paper.listener.ConnectionListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

@Getter
@Accessors(fluent = true)
public class MessagePlugin extends JavaPlugin {
    private final Metrics metrics = new Metrics(this, 19942);
    private final ComponentBundle bundle = new ComponentBundle(new File(getDataFolder(), "translations"), audience ->
            audience instanceof Player player ? player.locale() : Locale.US)
            .register("vanilla_msg", Locale.US)
            .register("vanilla_msg_german", Locale.GERMANY)
            .fallback(Locale.US);

    @Override
    public void onLoad() {
        bundle().miniMessage(MiniMessage.builder()
                .tags(TagResolver.resolver(
                        TagResolver.standard(),
                        Placeholder.component("prefix", bundle().component(Locale.US, "prefix"))
                ))
                .build());
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(this), this);
        Bukkit.getCommandMap().register(getName(), new MessageCommand(this));
        Bukkit.getCommandMap().register(getName(), new ReplyCommand(this));
    }

    @Override
    public void onDisable() {
        metrics.shutdown();
    }

    private final Map<CommandSender, CommandSender> conversations = new WeakHashMap<>();
}
