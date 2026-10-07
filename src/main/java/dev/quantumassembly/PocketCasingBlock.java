package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** A part of the Pocket Dimension Machine. Cannot be broken while the machine is assembled. */
public class PocketCasingBlock extends Block {
    public PocketCasingBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(Multiblock.ASSEMBLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(Multiblock.ASSEMBLED);
    }

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
}
