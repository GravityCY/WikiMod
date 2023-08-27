package me.gravityio.wikimod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.gravityio.wikimod.Helper;
import me.gravityio.wikimod.WikiMod;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.system.Platform;

import java.io.IOException;

public class WikiCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(WikiCommand.build());
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        var wiki = ClientCommandManager.literal("wiki");
        var arg = makeArg();
        wiki.then(arg);
        return wiki;
    }

    private static RequiredArgumentBuilder<FabricClientCommandSource, String> makeArg() {
        var arg = ClientCommandManager.argument("arg", StringArgumentType.greedyString());
        arg.executes(context -> {
            var input = StringArgumentType.getString(context, "arg");
            var id = new Identifier(input);
            Helper.openWiki(context.getSource().getClient(), id.getNamespace(), id.getPath());
            return 1;
        });
        return arg;
    }
}
