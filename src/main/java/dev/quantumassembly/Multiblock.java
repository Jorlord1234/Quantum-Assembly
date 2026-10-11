package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** The pocket multiblock: a 3 x 3 x 3 cube with the Core in the middle and the Controls in the middle of one side. */
public final class Multiblock {
    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");

    private Multiblock() {
    }

    /** A Pocket Casing, or a block that counts as one (the Kinetic Port). */
    public static boolean isCasing(BlockState state) {
        return state.is(ModBlocks.POCKET_CASING.get()) || state.is(ModBlocks.KINETIC_PORT.get());
    }

    public static boolean isPart(BlockState state) {
        return isCasing(state) || state.is(ModBlocks.POCKET_CORE.get()) || state.is(ModBlocks.POCKET_CONTROLS.get());
    }

    public static BlockPos centerOf(BlockState controls, BlockPos controlsPos) {
        return controlsPos.relative(controls.getValue(PocketControlsBlock.FACING).getOpposite());
    }

    public static Component tryAssemble(Level level, BlockPos controlsPos, ServerPlayer player) {
        BlockState controls = level.getBlockState(controlsPos);
        BlockPos center = centerOf(controls, controlsPos);
        int casings = 0;
        boolean core = level.getBlockState(center).is(ModBlocks.POCKET_CORE.get());
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (isCasing(level.getBlockState(center.offset(dx, dy, dz)))) {
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
                    if (isPart(s)) {
                        level.setBlock(pos, s.setValue(ASSEMBLED, true), 3);
                    }
                }
            }
        }
        BlockEntity blockEntity = level.getBlockEntity(controlsPos);
        if (blockEntity instanceof PocketControlsBlockEntity be) {
            be.setOwner(player.getUUID());
            PocketManager.rememberControls(be);
        }
        return Component.translatable("message.quantum_assembly.mb_formed");
    }

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
                        level.setBlock(pos, s.setValue(ASSEMBLED, false), 3);
                    }
                }
            }
        }
    }
}
