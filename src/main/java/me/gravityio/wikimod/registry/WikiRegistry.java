package me.gravityio.wikimod.registry;

import java.util.HashMap;
import java.util.Map;

public class WikiRegistry {
    private static final Map<String, WikiFinder> NAMESPACE_TO_WIKI = new HashMap<>();
    public static void register(String id, WikiFinder wiki) {
        NAMESPACE_TO_WIKI.put(id, wiki);
    }
    public static void register(Map<String, WikiFinder> namespaceToWiki) {
        NAMESPACE_TO_WIKI.putAll(namespaceToWiki);
    }
    public static void clear() {
        NAMESPACE_TO_WIKI.clear();
    }
    public static WikiFinder getWikiFinder(String namespace) {
        return NAMESPACE_TO_WIKI.get(namespace);
    }
}
