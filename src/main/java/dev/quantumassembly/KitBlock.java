package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KitBlock extends FacingShapedBlock implements EntityBlock {
    public KitBlock(BlockBehaviour.Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KitBlockEntity(pos, state);
    }

    /** Only the client needs a ticker: it moves the lid. */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide && type == ModBlockEntities.KIT.get()) {
            return (lvl, pos, st, be) -> KitBlockEntity.clientTick(lvl, pos, st, (KitBlockEntity) be);
        }
        return null;
    }

    /** The server tells the clients how many players have the Kit open (the lid follows). */
    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(id, param);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof KitBlockEntity kit) {
            player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new KitMenu(id, inventory, kit),
                    Component.translatable("block.quantum_assembly.quantum_assembly_kit")), pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof KitBlockEntity kit) {
            kit.dropStorage(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
