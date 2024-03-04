package net.thenextlvl.msg.paper.command;

import lombok.Getter;
import net.thenextlvl.msg.paper.MessagePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;

import java.util.Collections;
import java.util.List;

@Getter
public class ReplyCommand extends Command implements PluginIdentifiableCommand {
    private final MessagePlugin plugin;

    public ReplyCommand(MessagePlugin plugin) {
        super("reply", "Send a text reply to the player you last chatted with", "/reply [message]", List.of("r"));
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 1) {
            plugin.bundle().sendMessage(sender, "command.usage.reply");
            return true;
        }
        var player = plugin.conversations().get(sender);
        if (player == null) {
            plugin.bundle().sendMessage(sender, "conversation.running");
            return true;
        }
        MessageCommand.message(plugin, args, sender, player);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return Collections.emptyList();
    }
}
