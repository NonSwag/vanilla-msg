package net.thenextlvl.msg.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.msg.velocity.MessagePlugin;
import org.jetbrains.annotations.NotNull;

public class ReplyCommand {
    public static BrigadierCommand create(MessagePlugin plugin) {
        var command = LiteralArgumentBuilder.<CommandSource>literal("reply")
                .requires(source -> source instanceof Player)
                .executes(usage(plugin))
                .then(message(plugin))
                .build();
        return new BrigadierCommand(command);
    }

    @NotNull
    private static Command<CommandSource> usage(MessagePlugin plugin) {
        return context -> {
            plugin.bundle().sendMessage(context.getSource(), "command.usage.reply");
            return Command.SINGLE_SUCCESS;
        };
    }

    private static RequiredArgumentBuilder<CommandSource, String> message(MessagePlugin plugin) {
        return RequiredArgumentBuilder.<CommandSource, String>argument("message", StringArgumentType.greedyString())
                .requires(source -> source instanceof Player)
                .executes(context -> {
                    var source = (Player) context.getSource();
                    var player = plugin.conversations().get(source);
                    if (player == null) {
                        plugin.bundle().sendMessage(source, "conversation.running");
                        return Command.SINGLE_SUCCESS;
                    }
                    var sender = Placeholder.parsed("sender", source.getUsername());
                    var receiver = Placeholder.parsed("receiver", player.getUsername());
                    var message = Placeholder.parsed("message", context.getArgument("message", String.class));
                    plugin.bundle().sendMessage(source, "message.out", receiver, sender, message);
                    plugin.bundle().sendMessage(player, "message.in", receiver, sender, message);
                    plugin.conversations().put(player, source);
                    return Command.SINGLE_SUCCESS;
                });
    }
}
