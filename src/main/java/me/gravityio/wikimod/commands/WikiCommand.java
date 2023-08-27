package me.gravityio.wikimod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.gravityio.wikimod.Helper;
import me.gravityio.wikimod.ModConfig;
import me.gravityio.wikimod.WikiMod;
import me.gravityio.wikimod.registry.WikiRegistry;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WikiCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(WikiCommand.build());
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        var wiki = ClientCommandManager.literal("wiki");
        var namespaceArgument = WikiCommand.buildNamespaceArgument();
        wiki.then(namespaceArgument);
        wiki.executes(context -> {
            context.getSource().getPlayer().sendMessage(Text.translatable("messages.wikimod.command.wiki.help"));
            return 1;
        });

        return wiki;
    }

    private static RequiredArgumentBuilder<FabricClientCommandSource, String> buildNamespaceArgument() {
        var namespaceArg = ClientCommandManager.argument("namespace", NamespaceArgument.namespace());
        var pathArg = WikiCommand.buildPathArgument();
        namespaceArg.then(pathArg);
        namespaceArg.executes(context -> {
            var path = NamespaceArgument.getNamespace(context, "namespace");
            Helper.openWiki(context.getSource().getClient(), "minecraft", path);
           return 1;
        });
        return namespaceArg;
    }

    private static RequiredArgumentBuilder<FabricClientCommandSource, String> buildPathArgument() {
        var arg = ClientCommandManager.argument("path", StringArgumentType.greedyString());
        arg.executes(context -> {
            var namespace = NamespaceArgument.getNamespace(context, "namespace");
            var path = StringArgumentType.getString(context, "path");
            if (!WikiRegistry.exists(namespace)) {
                path = namespace + " " + path;
                namespace = "minecraft";
            }
            Helper.openWiki(context.getSource().getClient(), namespace, path);
            return 1;
        });
        return arg;
    }
}
