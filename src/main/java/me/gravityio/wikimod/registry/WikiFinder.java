package me.gravityio.wikimod.registry;

import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;

public interface WikiFinder {
    void getPage(MinecraftClient client, String path, Consumer<String> onReceived);
}
