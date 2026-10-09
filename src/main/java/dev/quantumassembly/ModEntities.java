package dev.quantumassembly;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, QuantumAssembly.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<StarMite>> STAR_MITE = ENTITIES.register("star_mite",
            () -> EntityType.Builder.<StarMite>of(StarMite::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.3F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(8)
                    .build("star_mite"));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(STAR_MITE.get(), StarMite.createAttributes().build());
    }
}
