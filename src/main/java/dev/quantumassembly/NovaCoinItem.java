package dev.quantumassembly;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

/** Right-click: opens the Waystones teleport menu (like a Warp Stone). Teleports are free (see WaystonesCompat). */
public class NovaCoinItem extends Item {
    public NovaCoinItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!ModList.get().isLoaded("waystones")) {
            return InteractionResultHolder.pass(stack);
        }
        Item warpStone = BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath("waystones", "warp_stone")).orElse(null);
        if (warpStone == null) {
            return InteractionResultHolder.pass(stack);
        }
        if (!level.isClientSide) {
            level.playSound(null, player, SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.1F, 2.0F);
            // Open the exact same menu the Warp Stone opens. The coin itself is never used up.
            warpStone.finishUsingItem(new ItemStack(warpStone), level, player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
