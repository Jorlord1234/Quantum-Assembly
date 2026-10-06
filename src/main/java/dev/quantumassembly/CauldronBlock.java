package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.CookingPotBlock;

/** The Master Chef's Cauldron: a golden Farmer's Delight cooking pot. */
public class CauldronBlock extends CookingPotBlock {
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 10, 14);

    public CauldronBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
