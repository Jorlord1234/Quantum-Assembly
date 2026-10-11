package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Small helpers shared by the Kinetic Port and the Pocket Power Output. */
final class PocketPower {
    private PocketPower() {
    }

    /** The assembled Pocket Controls within reach of a block of the multiblock shell (the shell is 3 x 3 x 3). */
    static PocketControlsBlockEntity controlsNear(Level level, BlockPos pos) {
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))) {
            if (!level.hasChunkAt(p)) {
                continue;
            }
            BlockState state = level.getBlockState(p);
            if (state.is(ModBlocks.POCKET_CONTROLS.get()) && state.getValue(Multiblock.ASSEMBLED)
                    && level.getBlockEntity(p) instanceof PocketControlsBlockEntity controls) {
                return controls;
            }
        }
        return null;
    }
}
