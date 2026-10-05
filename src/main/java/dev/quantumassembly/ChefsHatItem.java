package dev.quantumassembly;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** A very tall hat that never breaks and gives Farmer's Delight Nourishment while worn. */
public class ChefsHatItem extends ArmorItem {
    private static final ResourceLocation NOURISHMENT = ResourceLocation.fromNamespaceAndPath("farmersdelight", "nourishment");

    public ChefsHatItem(Properties properties) {
        super(ModArmorMaterials.CHEF, ArmorItem.Type.HELMET, properties.stacksTo(1));
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
