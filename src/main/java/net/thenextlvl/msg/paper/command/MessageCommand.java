package net.thenextlvl.msg.paper.command;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.msg.paper.MessagePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class MessageCommand extends Command implements PluginIdentifiableCommand {
    private final MessagePlugin plugin;

    public MessageCommand(MessagePlugin plugin) {
        super("msg", "Start a conversation with another player", "/msg [player] [message]", List.of("tell", "w"));
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            plugin.bundle().sendMessage(sender, "command.usage.msg");
            return true;
        }
        var player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            plugin.bundle().sendMessage(sender, "player.unknown", Placeholder.parsed("player", args[0]));
            return true;
        }
        if (player.equals(sender)) {
            plugin.bundle().sendMessage(sender, "message.self");
            return true;
        }
        message(plugin, Arrays.copyOfRange(args, 1, args.length), sender, player);
        return true;
    }

    static void message(MessagePlugin plugin, String[] args, CommandSender source, CommandSender target) {
        var sender = Placeholder.parsed("sender", source.getName());
        var receiver = Placeholder.parsed("receiver", target.getName());
        var message = Placeholder.parsed("message", String.join(" ", args));
        plugin.bundle().sendMessage(source, "message.out", receiver, sender, message);
        plugin.bundle().sendMessage(target, "message.in", receiver, sender, message);
        plugin.conversations().put(target, source);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return args.length <= 1 ? Bukkit.getOnlinePlayers().stream()
                .filter(all -> !all.equals(sender))
                .map(Player::getName)
                .filter(s -> s.contains(args[args.length - 1]))
                .toList() : Collections.emptyList();
    }
}
