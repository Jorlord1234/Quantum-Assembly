package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/** The Quantum Assembly Kit block: right-click items in, shift + empty hand takes them back. */
public class KitBlock extends FacingShapedBlock implements EntityBlock {
    public KitBlock(Properties properties, VoxelShape northShape) {
        super(properties, northShape);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KitBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != ModBlockEntities.KIT.get()) {
            return null;
        }
        return (lvl, pos, st, be) -> ((KitBlockEntity) be).serverTick((ServerLevel) lvl, pos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        int slot = KitBlockEntity.slotFor(stack);
        if (slot < 0) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; // wrong items do nothing
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof KitBlockEntity kit) {
            player.displayClientMessage(Component.literal(kit.tryInsert(slot, stack, player)), true);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof KitBlockEntity kit) {
            if (player.isShiftKeyDown()) {
                kit.giveBack(player);
                player.displayClientMessage(Component.literal("Items returned. " + kit.describe()), true);
            } else {
                player.openMenu(new SimpleMenuProvider(
                        (id, inventory, p) -> new KitMenu(id, inventory, kit),
                        Component.translatable("block.quantum_assembly.quantum_assembly_kit")), pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof KitBlockEntity kit) {
            kit.dropAll(level, pos);
            kit.dropStorage(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
