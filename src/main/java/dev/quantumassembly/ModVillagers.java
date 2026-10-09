package dev.quantumassembly;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The Quantum Engineer: a villager whose job-site block is the Quantum Assembly Kit. */
public class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, QuantumAssembly.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(Registries.VILLAGER_PROFESSION, QuantumAssembly.MODID);

    public static final DeferredHolder<PoiType, PoiType> QUANTUM_BENCH = POI_TYPES.register("quantum_engineer",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.QUANTUM_ASSEMBLY_KIT.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> QUANTUM_ENGINEER = PROFESSIONS.register("quantum_engineer",
            () -> new VillagerProfession("quantum_engineer",
                    holder -> holder.is(QUANTUM_BENCH.getKey()),
                    holder -> holder.is(QUANTUM_BENCH.getKey()),
                    ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_TOOLSMITH));
}
