package me.gravityio.wikimod;

import me.gravityio.wikimod.mixins.impl.SlotAccessor;
import me.gravityio.wikimod.registry.WikiRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.system.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Helper {
    private static final long DELAY = 100;
    private static long lastClick = System.currentTimeMillis();
    public static void openWiki(MinecraftClient client, String namespace, String thing) {
        var wikiFinder = WikiRegistry.getWikiFinder(namespace);
        if (wikiFinder == null) {
            client.player.sendMessage(Text.translatable("messages.wikimod.open_thing.unknown_wiki", namespace), true);
            return;
        }
        wikiFinder.getPage(client, thing, page -> {

            final Text message;
            boolean success = Helper.openUrlWithDefaultBrowser(page);
            if (success) {
                message = Text.translatable("messages.wikimod.open_thing.success", namespace + ":" + thing);
            } else {
                message = Text.translatable("messages.wikimod.open_thing.failure", namespace + ":" + thing);
            }
            client.player.sendMessage(message, true);
        });
    }
    public static void openWorldTarget(MinecraftClient client) {
        if (client.world == null || client.crosshairTarget == null) return;
        if (client.crosshairTarget instanceof BlockHitResult)
            openBlock(client);
        else if (client.crosshairTarget instanceof EntityHitResult) {
            openEntity(client);
        }
    }
    public static void openEntity(MinecraftClient client) {
        var entityHitResult = (EntityHitResult) client.crosshairTarget;
        var id = Registries.ENTITY_TYPE.getId(entityHitResult.getEntity().getType());
        openWiki(client, id.getNamespace(), id.getPath());
    }
    public static void openBlock(MinecraftClient client) {
        var blockHitResult = (BlockHitResult) client.crosshairTarget;
        var id = Registries.BLOCK.getId(client.world.getBlockState(blockHitResult.getBlockPos()).getBlock());
        var namespace = id.getNamespace();
        WikiRegistry.getWikiFinder(namespace);
        openWiki(client, id.getNamespace(), id.getPath());
    }
    public static void openSlot(MinecraftClient client) {
        SlotAccessor accessor = (SlotAccessor) client.currentScreen;
        var slot = accessor.getFocusedSlot();
        if (slot == null || System.currentTimeMillis() - lastClick < DELAY) return;
        lastClick = System.currentTimeMillis();
        var stack = slot.getStack();
        var id = Registries.ITEM.getId(stack.getItem());
        String namespace = id.getNamespace();
        String path = id.getPath();

        if (stack.isOf(Items.ENCHANTED_BOOK)) {
            var enchantMap = EnchantmentHelper.get(stack);
            for (Map.Entry<Enchantment, Integer> enchantEntry : enchantMap.entrySet()) {
                var enchantId = Registries.ENCHANTMENT.getId(enchantEntry.getKey());
                if (enchantId == null) continue;
                openWiki(client, enchantId.getNamespace(), enchantId.getPath());
            }
            return;
        }

        openWiki(client, namespace, path);
    }
    public static List<String> toList(Map<String, String> map, String separator) {
        var out = new ArrayList<String>();
        for (Map.Entry<String, String> entries : map.entrySet()) {
            out.add(entries.getKey() + separator + entries.getValue());
        }
        return out;
    }

    public static boolean openUrlWithDefaultBrowser(String url) {
        var rt = Runtime.getRuntime();
        var run = switch (Platform.get()) {
            case WINDOWS -> "explorer \"%s\"".formatted(url);
            case MACOSX, LINUX -> "open " + url;
        };
        WikiMod.DEBUG("Running {}", run);
        try {
            rt.exec(run);
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
