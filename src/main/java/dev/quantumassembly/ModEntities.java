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

    public static final DeferredHolder<EntityType<?>, EntityType<StarCrawler>> STAR_CRAWLER = ENTITIES.register("star_crawler",
            () -> EntityType.Builder.<StarCrawler>of(StarCrawler::new, MobCategory.MONSTER)
                    .sized(1.3F, 0.9F)
                    .eyeHeight(0.55F)
                    .clientTrackingRange(8)
                    .build("star_crawler"));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(STAR_CRAWLER.get(), StarCrawler.createAttributes().build());
    }
}
