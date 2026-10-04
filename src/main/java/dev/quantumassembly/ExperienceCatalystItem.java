package dev.quantumassembly;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public class ExperienceCatalystItem extends Item {
    public ExperienceCatalystItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    /** How many millibuckets of experience are stored. */
    public static int stored(ItemStack stack) {
        SimpleFluidContent content = stack.get(ModDataComponents.FLUID.get());
        return content == null ? 0 : content.copy().getAmount();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stored(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int capacity = Config.capacityMb();
        return Math.min(13, Math.round(13.0F * stored(stack) / capacity));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x41FFDE;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String have = String.format("%.1f", stored(stack) / 1000.0);
        String max = String.valueOf(Config.capacityMb() / 1000);
        tooltip.add(Component.translatable("tooltip.quantum_assembly.experience", have, max));
    }
}
