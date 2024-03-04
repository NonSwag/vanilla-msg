package net.thenextlvl.msg.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.i18n.file.ComponentBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.msg.velocity.command.MessageCommand;
import net.thenextlvl.msg.velocity.command.ReplyCommand;
import net.thenextlvl.msg.velocity.command.brigadier.PlayerArgumentProvider;
import net.thenextlvl.msg.velocity.listener.ConnectionListener;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

@Plugin(
        id = "vanilla-msg",
        name = "VanillaMSG",
        version = "1.0.3",
        url = "https://thenextlvl.net",
        description = "A simple msg plugin using brigadier",
        authors = "NonSwag"
)
@Getter
@Accessors(fluent = true)
@FieldsAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public class MessagePlugin {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataFolder;
    private final ComponentBundle bundle;
    private final Metrics.Factory metricsFactory;

    @Inject
    public MessagePlugin(ProxyServer server, Logger logger, @DataDirectory Path dataFolder, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
        bundle = new ComponentBundle(new File(dataFolder().toFile(), "translations"), audience ->
                audience instanceof Player player ? player.getPlayerSettings().getLocale() : Locale.US)
                .register("vanilla_msg", Locale.US)
                .register("vanilla_msg_german", Locale.GERMANY)
                .fallback(Locale.US);
        bundle().miniMessage(MiniMessage.builder()
                .tags(TagResolver.resolver(
                        TagResolver.standard(),
                        Placeholder.component("prefix", bundle().component(Locale.US, "prefix"))
                ))
                .build());
        this.metricsFactory = metricsFactory;
    }

    private final PlayerArgumentProvider playerArgumentProvider = new PlayerArgumentProvider(this);
    private final Map<Player, Player> conversations = new WeakHashMap<>();

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        metricsFactory().make(this, 19942);
        server().getEventManager().register(this, new ConnectionListener(this));
        server().getCommandManager().register("msg", MessageCommand.create(this), "tell", "w");
        server().getCommandManager().register("reply", ReplyCommand.create(this), "r");
    }
}
