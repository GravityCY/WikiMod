package me.gravityio.wikimod;

import me.gravityio.wikimod.registry.FandomPageFinder;

public class SupportedCustomWikis {
    public static boolean isSupported(String url) {
        return FandomPageFinder.isFandomUrl(url);
    }
}
