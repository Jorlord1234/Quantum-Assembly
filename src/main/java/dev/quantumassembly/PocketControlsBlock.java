package dev.quantumassembly;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

/** The Pocket Controls: right-click to build the machine, then to open the upgrade screen. */
public class PocketControlsBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<PocketControlsBlock> CODEC = simpleCodec(PocketControlsBlock::new);

    public PocketControlsBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(Multiblock.ASSEMBLED, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, Multiblock.ASSEMBLED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PocketControlsBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        if (!state.getValue(Multiblock.ASSEMBLED)) {
            player.displayClientMessage(Multiblock.tryAssemble(level, pos, serverPlayer), true);
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof PocketControlsBlockEntity be) {
            if (be.getOwner() != null && !be.getOwner().equals(player.getUUID()) && !player.isCreative()) {
                player.displayClientMessage(Component.translatable("message.quantum_assembly.not_yours"), true);
                return InteractionResult.SUCCESS;
            }
            player.openMenu(new SimpleMenuProvider(
                    (id, inventory, p) -> new PocketControlsMenu(id, inventory, be),
                    Component.translatable("block.quantum_assembly.pocket_controls")), pos);
        }
        return InteractionResult.SUCCESS;
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
