package dev.quantumassembly;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, QuantumAssembly.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KitBlockEntity>> KIT =
            BLOCK_ENTITIES.register("quantum_assembly_kit",
                    () -> BlockEntityType.Builder.of(KitBlockEntity::new, ModBlocks.QUANTUM_ASSEMBLY_KIT.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PocketControlsBlockEntity>> CONTROLS =
            BLOCK_ENTITIES.register("pocket_controls",
                    () -> BlockEntityType.Builder.of(PocketControlsBlockEntity::new, ModBlocks.POCKET_CONTROLS.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GateBlockEntity>> GATE =
            BLOCK_ENTITIES.register("quantum_gate",
                    () -> BlockEntityType.Builder.of(GateBlockEntity::new, ModBlocks.QUANTUM_GATE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NebulaBurnerBlockEntity>> NEBULA_BURNER =
            BLOCK_ENTITIES.register("nebula_blaze_burner",
                    () -> BlockEntityType.Builder.of(NebulaBurnerBlockEntity::new, ModBlocks.NEBULA_BLAZE_BURNER.get()).build(null));
}
