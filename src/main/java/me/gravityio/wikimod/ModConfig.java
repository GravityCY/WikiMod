package me.gravityio.wikimod;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.GsonConfigInstance;
import dev.isxander.yacl3.impl.controller.StringControllerBuilderImpl;
import me.gravityio.wikimod.registry.FandomPageFinder;
import me.gravityio.wikimod.registry.WikiRegistry;
import me.gravityio.wikimod.registry.WikiFinder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ModConfig {
    public static ModConfig INSTANCE;
    public static final String SEPARATOR = ":";
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("wikimod.json");
    private static final GsonBuilder builder = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
            .registerTypeHierarchyAdapter(Color.class, new GsonConfigInstance.ColorTypeAdapter());

    public static GsonConfigInstance<ModConfig> GSON = GsonConfigInstance.createBuilder(ModConfig.class)
            .setPath(PATH)
            .overrideGsonBuilder(builder)
            .build();

    // TODO: CARPET, AE2, ORIGINS, ULTRIS, SUPPLEMENTARIES

    @ConfigEntry
    public Map<String, String> wikis = Map.of(
            "minecraft", "minecraft",
            "create", "create",
            "byg", "oh-the-biomes-youll-go",
            "mca", "minecraft-comes-alive-reborn"
    );

    public List<String> getWikis() {
        return Helper.toList(this.wikis, (key, value) -> {
            if (!key.equals(value)) return key + SEPARATOR + value;
            return key;
        });
    }

    public void setWikis(List<String> mapList) {
        this.wikis.clear();
        for (String s : mapList) {
            var split = s.split(SEPARATOR, 2);
            String key;
            String value;

            if (split.length == 1) {
                key = split[0];
                value = split[0];
            } else if (split.length == 2) {
                key = split[0];
                value = doSanitizeFandomURL(split[1]);
            } else {
                continue;
            }

            if (key == null) continue;
            this.wikis.put(key, value);
        }

        this.onUpdateWikis();
    }

    public void onUpdateWikis() {
        WikiRegistry.clear();
        for (Map.Entry<String, String> entry : this.wikis.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            WikiFinder regItem = new FandomPageFinder(getFandomURLFromSubdomain(value));
            WikiRegistry.register(key, regItem);
        }
    }

    public static String getFandomURLFromSubdomain(String subdomain) {
        return "https://" + subdomain.toLowerCase() + ".fandom.com";
    }

    public static String doSanitizeFandomURL(String URL) {
        return URL.toLowerCase().replace("https://", "").replace("fandom.com", "");
    }


    public static Screen doBuildScreen(Screen parent) {
        return YetAnotherConfigLib.create(ModConfig.GSON, (defaults, config, builder) -> {
            builder.title(Text.translatable("wikimod.config.title"));

            var mainCategory = ConfigCategory.createBuilder()
                    .name(Text.translatable("wikimod.config.category.main.title"));

            var wikis = ListOption.<String>createBuilder()
                    .name(Text.translatable("wikimod.config.category.main.wikis.label"))
                    .description(OptionDescription.of(Text.translatable("wikimod.config.category.main.wikis.description")))
                    .controller(StringControllerBuilderImpl::new)
                    .initial("")
                    .binding(defaults.getWikis(), config::getWikis, config::setWikis)
                    .build();

            mainCategory.option(wikis);

            builder.category(mainCategory.build());
            return builder;
        }).generateScreen(parent);
    }
    
}
