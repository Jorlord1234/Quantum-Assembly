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

    public static final DeferredBlock<Block> STARGLASS_ORE = BLOCKS.registerSimpleBlock("starglass_ore",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE));

    public static final DeferredBlock<ShapedBlock> QUANTUM_ASSEMBLY_KIT = BLOCKS.registerBlock("quantum_assembly_kit",
            props -> new ShapedBlock(props, KIT_SHAPE),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion());

    public static final DeferredBlock<ShapedBlock> MASTER_CHEFS_CAULDRON = BLOCKS.registerBlock("master_chefs_cauldron",
            props -> new ShapedBlock(props, CAULDRON_SHAPE),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion());
}
