package dev.quantumassembly;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(QuantumAssembly.MODID);

    private static final VoxelShape KIT_SHAPE = Shapes.or(
            Block.box(1, 0, 4, 15, 9, 12),
            Block.box(4, 9, 7, 12, 11, 9));

    private static final VoxelShape CAULDRON_SHAPE = Shapes.or(
            Block.box(2, 0, 2, 14, 10, 14),
            Block.box(0, 4, 6, 2, 7, 10),
            Block.box(14, 4, 6, 16, 7, 10));

    private static final VoxelShape GATE_SHAPE = Block.box(0, 0, 6, 16, 16, 10);

    public static final DeferredBlock<QuantumGateBlock> QUANTUM_GATE = BLOCKS.registerBlock("quantum_gate",
            props -> new QuantumGateBlock(props, GATE_SHAPE),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(3.0F, 8.0F)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> 10)
                    .noOcclusion());

    private static BlockBehaviour.Properties partProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(5.0F, 1200.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHERITE_BLOCK)
                .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK)
                .lightLevel(state -> state.getValue(Multiblock.ASSEMBLED) ? 6 : 0);
    }

    public static final DeferredBlock<PocketCasingBlock> POCKET_CASING = BLOCKS.registerBlock("pocket_casing", PocketCasingBlock::new, partProps());
    public static final DeferredBlock<PocketCoreBlock> POCKET_CORE = BLOCKS.registerBlock("pocket_core", PocketCoreBlock::new, partProps());
    public static final DeferredBlock<PocketControlsBlock> POCKET_CONTROLS = BLOCKS.registerBlock("pocket_controls", PocketControlsBlock::new, partProps());

    public static final DeferredBlock<Block> STARGLASS_ORE = BLOCKS.registerSimpleBlock("starglass_ore",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE));

    public static final DeferredBlock<KitBlock> QUANTUM_ASSEMBLY_KIT = BLOCKS.registerBlock("quantum_assembly_kit",
            props -> new KitBlock(props, KIT_SHAPE),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion());

    public static final DeferredBlock<CauldronBlock> MASTER_CHEFS_CAULDRON = BLOCKS.registerBlock("master_chefs_cauldron",
            CauldronBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(CauldronBlock.COOKING) ? 10 : 0)
                    .noOcclusion());

    public static final DeferredBlock<NebulaBurnerBlock> NEBULA_BLAZE_BURNER = BLOCKS.registerBlock("nebula_blaze_burner",
            NebulaBurnerBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()
                    .lightLevel(state -> NebulaBurnerBlock.isLit(state) ? 15 : 0));
}
