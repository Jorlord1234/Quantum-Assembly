package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** The 3x3x3 Pocket Dimension Machine: 1 Core in the middle, 1 Controls in the middle of one side, 25 Casings. */
public final class Multiblock {
    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");

    private Multiblock() {
    }

    public static boolean isPart(BlockState state) {
        return state.is(ModBlocks.POCKET_CASING.get()) || state.is(ModBlocks.POCKET_CORE.get()) || state.is(ModBlocks.POCKET_CONTROLS.get());
    }

    /** The Core sits right behind the Controls. */
    public static BlockPos centerOf(BlockState controls, BlockPos controlsPos) {
        return controlsPos.relative(controls.getValue(PocketControlsBlock.FACING).getOpposite());
    }

    /** Checks the shape and, if it is right, locks it. Returns a message to show the player. */
    public static Component tryAssemble(Level level, BlockPos controlsPos, ServerPlayer player) {
        BlockState controls = level.getBlockState(controlsPos);
        BlockPos center = centerOf(controls, controlsPos);
        int casings = 0;
        boolean core = level.getBlockState(center).is(ModBlocks.POCKET_CORE.get());
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (level.getBlockState(pos).is(ModBlocks.POCKET_CASING.get())) {
                        casings++;
                    }
                }
            }
        }
        if (!core || casings < 25) {
            return Component.translatable("message.quantum_assembly.mb_missing", core ? 0 : 1, 25 - casings);
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState s = level.getBlockState(pos);
                    if (!isPart(s)) {
                        continue;
                    }
                    level.setBlock(pos, s.setValue(ASSEMBLED, true), Block.UPDATE_ALL);
                }
            }
        }
        if (level.getBlockEntity(controlsPos) instanceof PocketControlsBlockEntity be) {
            be.setOwner(player.getUUID());
        }
        return Component.translatable("message.quantum_assembly.mb_formed");
    }

    /** Unlocks all 27 blocks so they can be mined with a diamond pickaxe. */
    public static void disassemble(Level level, BlockPos controlsPos) {
        BlockState controls = level.getBlockState(controlsPos);
        if (!controls.is(ModBlocks.POCKET_CONTROLS.get())) {
            return;
        }
        BlockPos center = centerOf(controls, controlsPos);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState s = level.getBlockState(pos);
                    if (isPart(s) && s.getValue(ASSEMBLED)) {
                        level.setBlock(pos, s.setValue(ASSEMBLED, false), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }
}
