package me.gravityio.wikimod.registry;

import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;

public class AE2PageFinder implements WikiFinder {
    public static String URL = "https://guide.appliedenergistics.org/#/";
    public static String URL_V = URL + "1.19.4/";
    public static String URL_IBM = URL_V + "ae2:item-blocks-machines/";
    public static String F_URL_IBM = URL_IBM + "%s.md";

    @Override
    public void getPage(MinecraftClient client, String path, Consumer<String> onReceived) {
        onReceived.accept(F_URL_IBM.formatted(path));
    }
}
