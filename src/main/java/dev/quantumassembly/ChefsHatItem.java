package dev.quantumassembly;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** A helmet that never breaks and gives Farmer's Delight Nourishment while worn. */
public class ChefsHatItem extends Item implements Equipable {
    private static final ResourceLocation NOURISHMENT = ResourceLocation.fromNamespaceAndPath("farmersdelight", "nourishment");

    public ChefsHatItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return swapWithEquipmentSlot(this, level, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity living = (LivingEntity) entity;
        if (living.getItemBySlot(EquipmentSlot.HEAD) != stack || living.tickCount % 100 != 0) {
            return;
        }
        BuiltInRegistries.MOB_EFFECT.getHolder(NOURISHMENT).ifPresent(effect ->
                living.addEffect(new MobEffectInstance(effect, 220, 0, true, false, true)));
    }
}
