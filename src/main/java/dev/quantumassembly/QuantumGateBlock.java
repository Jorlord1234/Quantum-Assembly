package dev.quantumassembly;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Quantum Gate: a portal ring. Link it to a Pocket Controls (press "Link a Gate" there, then right-click the
 * Gate), after that just walk through it. Inside the pocket, a Gate brings you back out in front of the Gate you came
 * in through.
 */
public class QuantumGateBlock extends FacingShapedBlock implements EntityBlock {
    private static final String COOLDOWN = "quantum_assembly_gate_cooldown";
    private static final int COOLDOWN_TICKS = 60;

    public QuantumGateBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GateBlockEntity(pos, state);
    }

    /** You walk into the opening, like a portal. */
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
            return;
        }
        long now = level.getGameTime();
        if (player.getPersistentData().getLong(COOLDOWN) > now) {
            return;
        }
        player.getPersistentData().putLong(COOLDOWN, now + COOLDOWN_TICKS);
        handle(state, level, pos, player, true);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }
        serverPlayer.getPersistentData().putLong(COOLDOWN, level.getGameTime() + COOLDOWN_TICKS);
        handle(state, level, pos, serverPlayer, false);
        return InteractionResult.SUCCESS;
    }

    private void handle(BlockState state, Level level, BlockPos pos, ServerPlayer serverPlayer, boolean walkedIn) {
        if (level.dimension().equals(PocketManager.POCKET)) {
            PocketManager.leave(serverPlayer);
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof GateBlockEntity gate)) {
            return;
        }
        if (!walkedIn) {
            PocketManager.Pending pending = PocketManager.takePending(serverPlayer);
            if (pending != null) {
                gate.link(pending.dim(), pending.pos());
                serverPlayer.displayClientMessage(Component.translatable("message.quantum_assembly.gate_linked"), true);
                return;
            }
        }
        if (!gate.isLinked()) {
            serverPlayer.displayClientMessage(Component.translatable("message.quantum_assembly.gate_unlinked"), true);
            return;
        }
        ServerLevel target = serverPlayer.getServer().getLevel(
                ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(gate.getLinkDim())));
        BlockPos controlsPos = gate.getLinkPos();
        if (target == null || !target.getBlockState(controlsPos).is(ModBlocks.POCKET_CONTROLS.get())
                || !target.getBlockState(controlsPos).getValue(Multiblock.ASSEMBLED)
                || !(target.getBlockEntity(controlsPos) instanceof PocketControlsBlockEntity controls)
                || controls.getOwner() == null) {
            serverPlayer.displayClientMessage(Component.translatable("message.quantum_assembly.gate_dead"), true);
            return;
        }
        UUID owner = controls.getOwner();
        Direction front = state.getValue(FACING);
        PocketManager.enter(serverPlayer, owner,
                pos.getX() + 0.5 + front.getStepX() * 1.3, pos.getY(), pos.getZ() + 0.5 + front.getStepZ() * 1.3);
    }
}
