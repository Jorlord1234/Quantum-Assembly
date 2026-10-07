package dev.quantumassembly;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Quantum Gate: the way in. It must be linked to a Pocket Controls (press "Link a Gate" there,
 * then right-click the Gate). Inside the pocket, a Gate brings you back.
 */
public class QuantumGateBlock extends FacingShapedBlock implements EntityBlock {
    public QuantumGateBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GateBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }
        if (level.dimension().equals(PocketManager.POCKET)) {
            PocketManager.leave(serverPlayer);
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof GateBlockEntity gate)) {
            return InteractionResult.SUCCESS;
        }
        PocketManager.Pending pending = PocketManager.takePending(serverPlayer);
        if (pending != null) {
            gate.link(pending.dim(), pending.pos());
            player.displayClientMessage(Component.translatable("message.quantum_assembly.gate_linked"), true);
            return InteractionResult.SUCCESS;
        }
        if (!gate.isLinked()) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.gate_unlinked"), true);
            return InteractionResult.SUCCESS;
        }
        ServerLevel target = serverPlayer.getServer().getLevel(
                ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(gate.getLinkDim())));
        BlockPos controlsPos = gate.getLinkPos();
        if (target == null || !target.getBlockState(controlsPos).is(ModBlocks.POCKET_CONTROLS.get())
                || !target.getBlockState(controlsPos).getValue(Multiblock.ASSEMBLED)
                || !(target.getBlockEntity(controlsPos) instanceof PocketControlsBlockEntity controls)
                || controls.getOwner() == null) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.gate_dead"), true);
            return InteractionResult.SUCCESS;
        }
        UUID owner = controls.getOwner();
        PocketManager.enter(serverPlayer, owner);
        return InteractionResult.SUCCESS;
    }
}
