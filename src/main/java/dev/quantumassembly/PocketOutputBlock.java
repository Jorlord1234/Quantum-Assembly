package dev.quantumassembly;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Pocket Power Output: place it inside your pocket space. It turns at the same speed as the Kinetic Port of your
 * multiblock and offers the stress capacity of your Power upgrade, so you can drive Create machines inside the pocket.
 */
public class PocketOutputBlock extends DirectionalKineticBlock implements IBE<PocketOutputBlockEntity> {
    public PocketOutputBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public Class<PocketOutputBlockEntity> getBlockEntityClass() {
        return PocketOutputBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PocketOutputBlockEntity> getBlockEntityType() {
        return ModBlockEntities.POCKET_OUTPUT.get();
    }
}
