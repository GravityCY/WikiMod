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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModConfig {
    public static ModConfig INSTANCE;
    private static final String SEPARATOR = ";";
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

    @ConfigEntry
    public Map<String, String> wikis = Map.of(
            "minecraft", "https://minecraft.fandom.com",
            "create", "https://create.fandom.com"
    );

    public List<String> getWikis() {
        return Helper.toList(this.wikis, SEPARATOR);
    }

    public void setWikis(List<String> mapList) {
        Map<String, String> map = new HashMap<>();
        for (String s : mapList) {
            var split = s.split(SEPARATOR, 2);
            if (split.length != 2) continue;
            var key = split[0];
            var value = split[1];
            if (key == null || value == null) continue;
            if (!SupportedCustomWikis.isSupported(value)) continue;
            map.put(key, value);
        }
        this.wikis = map;

        this.onUpdateWikis();
    }

    public void onUpdateWikis() {
        WikiRegistry.clear();
        for (Map.Entry<String, String> entry : this.wikis.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            WikiFinder regItem;
            if (FandomPageFinder.isFandomUrl(value)) {
                regItem = new FandomPageFinder(value);
            } else continue;
            WikiRegistry.register(key, regItem);
        }
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
