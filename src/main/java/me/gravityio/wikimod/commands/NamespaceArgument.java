package me.gravityio.wikimod.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.gravityio.wikimod.ModConfig;
import me.gravityio.wikimod.WikiMod;
import me.gravityio.wikimod.registry.WikiRegistry;
import net.minecraft.command.CommandRegistryAccess;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NamespaceArgument implements ArgumentType<String> {
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var input = builder.getRemaining();
        for (String namespace : WikiRegistry.getNamespaces()) {
            if (!namespace.startsWith(input)) continue;
            builder.suggest(namespace);
            if (namespace.equals(input)) break;
        }
        return builder.buildFuture();
    }

    public static NamespaceArgument namespace() {
        return new NamespaceArgument();
    }

    public static <S> String getNamespace(CommandContext<S> context, String name) {
        return context.getArgument(name, String.class);
    }
}
