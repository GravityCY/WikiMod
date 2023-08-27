package me.gravityio.wikimod.registry;

import me.gravityio.wikimod.lib.FandomAPI;
import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;

public class FandomPageFinder implements WikiFinder {
    public final String fandomUrl;
    public FandomPageFinder(String fandomUrl) {
        this.fandomUrl = fandomUrl;
    }

    public static boolean isFandomUrl(String url) {
        return url.contains("fandom.com");
    }

    @Override
    public void getPage(MinecraftClient client, String path, Consumer<String> onReceived) {
        FandomAPI.getMatchingPage(fandomUrl, path, onReceived);
    }
}
