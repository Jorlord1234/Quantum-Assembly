package dev.quantumassembly;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/** A block item that is NOT used up in crafting recipes (it comes back out). */
public class ReturningBlockItem extends BlockItem {
    public ReturningBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return stack.copyWithCount(1);
    }
}
