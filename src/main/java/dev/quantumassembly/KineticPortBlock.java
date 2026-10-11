package dev.quantumassembly;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * The Pocket Kinetic Port: a casing of the multiblock that takes Create rotation in through its shaft face. With the
 * Power upgrade, the rotation shows up again at the Pocket Power Output inside your pocket space. It counts as one of
 * the 25 casings when you build the multiblock.
 */
public class KineticPortBlock extends DirectionalKineticBlock implements IBE<KineticPortBlockEntity> {
    public KineticPortBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(Multiblock.ASSEMBLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(Multiblock.ASSEMBLED);
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
    public Class<KineticPortBlockEntity> getBlockEntityClass() {
        return KineticPortBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticPortBlockEntity> getBlockEntityType() {
        return ModBlockEntities.KINETIC_PORT.get();
    }

    /** Like the other parts: it cannot be broken, wrenched or blown up while the multiblock is assembled. */
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return state.getValue(Multiblock.ASSEMBLED) ? 0.0F : super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (!state.getValue(Multiblock.ASSEMBLED)) {
            super.onBlockExploded(state, level, pos, explosion);
        }
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return state.getValue(Multiblock.ASSEMBLED) ? InteractionResult.FAIL : super.onWrenched(state, context);
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        return state.getValue(Multiblock.ASSEMBLED) ? InteractionResult.FAIL : super.onSneakWrenched(state, context);
    }
}
