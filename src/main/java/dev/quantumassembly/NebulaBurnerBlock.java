package dev.quantumassembly;

import org.joml.Vector3f;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Nebula Blaze Burner (smithing: Nova Template + Blaze Burner + Dragon Head).
 * COLD until you right-click it with a Cake of Nebulae; then it is LIT with a purple flame.
 * It stays lit for a configurable time (nebulaBurnerLitSeconds, 0 = forever).
 * Put the Quantum Assembly Kit on top of a lit burner for the ritual.
 */
public class NebulaBurnerBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<NebulaBurnerBlock> CODEC = simpleCodec(NebulaBurnerBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(2, 4, 2, 14, 14, 14));

    private static final DustParticleOptions PURPLE = new DustParticleOptions(new Vector3f(0.62F, 0.18F, 1.0F), 1.4F);

    public NebulaBurnerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    public static boolean isLit(BlockState state) {
        return state.hasProperty(LIT) && state.getValue(LIT);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(LIT, false);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(ModItems.CAKE_OF_NEBULAE.get()) || isLit(state)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0F, 0.6F);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            int ticks = Config.burnerLitTicks();
            if (ticks > 0) {
                level.scheduleTick(pos, this, ticks);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /** The lit time ran out: the burner goes cold again. */
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isLit(state)) {
            level.setBlock(pos, state.setValue(LIT, false), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6F, 1.0F);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (isLit(state) && random.nextInt(2) == 0) {
            level.addParticle(PURPLE,
                    pos.getX() + 0.35 + random.nextDouble() * 0.3,
                    pos.getY() + 0.9 + random.nextDouble() * 0.3,
                    pos.getZ() + 0.35 + random.nextDouble() * 0.3,
                    0.0, 0.03, 0.0);
        }
    }
}
