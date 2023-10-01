package net.thenextlvl.msg.velocity.command.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.msg.velocity.MessagePlugin;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class PlayerArgumentProvider {
    private final MessagePlugin plugin;

    public Player parse(CommandContext<?> context, String name) {
        return plugin.server().getPlayer(context.getArgument(name, String.class)).orElse(null);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        plugin.server().getAllPlayers().stream()
                .filter(player -> !player.equals(context.getSource()))
                .map(Player::getUsername)
                .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
