package me.gravityio.wikimod;

import me.gravityio.wikimod.commands.NamespaceArgument;
import me.gravityio.wikimod.commands.WikiCommand;
import me.gravityio.wikimod.mixins.impl.SlotAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikiMod implements ModInitializer, PreLaunchEntrypoint {
    public static final String MOD_ID = "wikimod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final KeyBinding keybind = new KeyBinding("keybind.wikimod.open_thing", GLFW.GLFW_KEY_RIGHT_CONTROL, "category.keybind.wikimod");
    public static boolean DEBUG = false;

    public static void DEBUG(String s, Object... args) {
        if (DEBUG) LOGGER.info(s, args);
    }

    @Override
    public void onPreLaunch() {
        ModConfig.GSON.load();
        ModConfig.INSTANCE = ModConfig.GSON.getConfig();
        ModConfig.INSTANCE.onUpdateWikis();
    }

    @Override
    public void onInitialize() {
        DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();
        KeyBindingHelper.registerKeyBinding(keybind);
        ClientCommandRegistrationCallback.EVENT.register(WikiCommand::register);
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
        ArgumentTypeRegistry.registerArgumentType(new Identifier(WikiMod.MOD_ID, "namespace"), NamespaceArgument.class, ConstantArgumentSerializer.of(NamespaceArgument::namespace));
    }

    private void onEndClientTick(MinecraftClient client) {
        var handle = client.getWindow().getHandle();

        while (keybind.wasPressed()) {
            if (client.currentScreen == null) {
                DEBUG("Opening Target");
                Helper.openWorldTarget(client);
            }
        }

        if (client.currentScreen instanceof SlotAccessor
                && InputUtil.isKeyPressed(handle, KeyBindingHelper.getBoundKeyOf(keybind).getCode())) {
            DEBUG("Opening Item");
            Helper.openSlot(client);
        }
    }
}
