package dev.quantumassembly;

import java.lang.reflect.Method;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The in-between item of the Experience Catalyst. Shows the same progress bar as other
 * Create Sequenced Assembly items. Create's data is read through reflection, so this
 * mod does not need Create at compile time.
 */
public class IncompleteCatalystItem extends Item {
    private static final ResourceLocation COMPONENT_ID =
            ResourceLocation.fromNamespaceAndPath("create", "sequenced_assembly");
    private static Method progressMethod;

    public IncompleteCatalystItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @SuppressWarnings("unchecked")
    private static float progress(ItemStack stack) {
        try {
            DataComponentType<Object> type = (DataComponentType<Object>)
                    BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(COMPONENT_ID).orElse(null);
            if (type == null) {
                return 0.0F;
            }
            Object data = stack.get(type);
            if (data == null) {
                return 0.0F;
            }
            if (progressMethod == null) {
                progressMethod = data.getClass().getMethod("progress");
            }
            return ((Number) progressMethod.invoke(data)).floatValue();
        } catch (Throwable t) {
            return 0.0F;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(Math.max(0.0F, Math.min(1.0F, progress(stack))) * 13.0F);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float p = Math.max(0.0F, Math.min(1.0F, progress(stack)));
        int from = 0xFFC074;
        int to = 0x46FFE0;
        int r = Math.round(((from >> 16) & 0xFF) * (1 - p) + ((to >> 16) & 0xFF) * p);
        int g = Math.round(((from >> 8) & 0xFF) * (1 - p) + ((to >> 8) & 0xFF) * p);
        int b = Math.round((from & 0xFF) * (1 - p) + (to & 0xFF) * p);
        return (r << 16) | (g << 8) | b;
    }
}
