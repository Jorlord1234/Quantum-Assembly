package dev.quantumassembly;

import java.lang.reflect.Field;

import net.minecraft.core.BlockPos;
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

    public CauldronBlock(Properties properties) {
        super(properties);
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
