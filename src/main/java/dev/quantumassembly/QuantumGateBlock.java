package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Quantum Gate. Right-click it to go to your own pocket space.
 * Inside the pocket it brings you back. Right-click with Starglass to make your space bigger.
 */
public class QuantumGateBlock extends FacingShapedBlock {
    public QuantumGateBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(ModItems.STARGLASS.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            PocketManager.upgrade(serverPlayer);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (level.dimension().equals(PocketManager.POCKET)) {
                PocketManager.leave(serverPlayer);
            } else {
                PocketManager.enter(serverPlayer);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
