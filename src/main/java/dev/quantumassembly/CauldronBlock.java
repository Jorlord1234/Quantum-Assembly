package dev.quantumassembly;

import java.lang.reflect.Field;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.CookingPotBlock;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

/** The Master Chef's Cauldron: a golden Farmer's Delight cooking pot. */
public class CauldronBlock extends CookingPotBlock {
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 10, 14);
    private static Field dataField;

    /** True while a Nebula Batter is cooking into a Cake of Nebulae. */
    public static final BooleanProperty COOKING = BooleanProperty.create("cooking");
    private static final int COOK_TICKS = 600;
    private static final TagKey<Block> HEAT_SOURCES = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "heat_sources"));

    public CauldronBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(COOKING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COOKING);
    }

    /** Is there something hot under the Cauldron? */
    private static boolean heated(Level level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (below.getFluidState().is(net.minecraft.tags.FluidTags.LAVA) || below.is(BlockTags.FIRE)) {
            return true;
        }
        if (below.getBlock() instanceof NebulaBurnerBlock) {
            return NebulaBurnerBlock.isLit(below);
        }
        if (below.is(HEAT_SOURCES) || below.is(BlockTags.CAMPFIRES)) {
            return !below.hasProperty(BlockStateProperties.LIT) || below.getValue(BlockStateProperties.LIT);
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(below.getBlock());
        if ("create".equals(id.getNamespace()) && "blaze_burner".equals(id.getPath())) {
            for (Property<?> property : below.getProperties()) {
                if ("blaze".equals(property.getName())) {
                    return !"none".equals(String.valueOf(below.getValue(property)));
                }
            }
        }
        return false;
    }

    /** Right-click with Nebula Batter on a heated Cauldron: it cooks into a Cake of Nebulae (only possible here). */
    private ItemInteractionResult startCooking(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (state.getValue(COOKING)) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.cauldron_busy"), true);
            return ItemInteractionResult.SUCCESS;
        }
        if (!heated(level, pos)) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.cauldron_needs_heat"), true);
            return ItemInteractionResult.SUCCESS;
        }
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        level.setBlock(pos, state.setValue(COOKING, true), Block.UPDATE_ALL);
        level.scheduleTick(pos, this, COOK_TICKS);
        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 0.7F);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(COOKING)) {
            return;
        }
        level.setBlock(pos, state.setValue(COOKING, false), Block.UPDATE_ALL);
        // if the heat went out meanwhile the batter just comes back
        ItemStack out = heated(level, pos) ? new ItemStack(ModItems.CAKE_OF_NEBULAE.get()) : new ItemStack(ModItems.NEBULA_BATTER.get());
        Block.popResource(level, pos.above(), out);
        level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.6F, 1.4F);
        level.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 20, 0.2, 0.2, 0.2, 0.05);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (state.getValue(COOKING)) {
            level.addParticle(ParticleTypes.PORTAL, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5, pos.getY() + 0.8,
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5, 0.0, 0.6, 0.0);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /** Pick-block and tooltips should give our Cauldron, not a normal cooking pot. */
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.MASTER_CHEFS_CAULDRON.get());
    }

    /** Same as Farmer's Delight, but opens our own menu (the normal one closes at once on a different block). */
    @Override
    public ItemInteractionResult useItemOn(ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult result) {
        if (heldStack.is(ModItems.NEBULA_BATTER.get())) {
            return startCooking(heldStack, state, level, pos, player);
        }
        if (heldStack.isEmpty() && player.isShiftKeyDown()) {
            return super.useItemOn(heldStack, state, level, pos, player, hand, result); // flips the support, like a normal pot
        }
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CookingPotBlockEntity pot) {
                ItemStack serving = pot.useHeldItemOnMeal(heldStack);
                if (serving != ItemStack.EMPTY) {
                    if (!player.getInventory().add(serving)) {
                        player.drop(serving, false);
                    }
                } else {
                    ContainerData data = dataOf(pot);
                    if (data != null) {
                        player.openMenu(new SimpleMenuProvider(
                                (id, inventory, p) -> new CauldronMenu(id, inventory, pot, data),
                                pot.getDisplayName()), pos);
                    }
                }
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    private static ContainerData dataOf(CookingPotBlockEntity pot) {
        try {
            if (dataField == null) {
                dataField = CookingPotBlockEntity.class.getDeclaredField("cookingPotData");
                dataField.setAccessible(true);
            }
            return (ContainerData) dataField.get(pot);
        } catch (ReflectiveOperationException e) {
            QuantumAssembly.LOGGER.warn("Could not open the Cauldron screen", e);
            return null;
        }
    }
}
