package dev.quantumassembly;

import java.lang.reflect.Method;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

/**
 * Right-click a Create Blaze Burner with a Catalyst that holds enough Liquid Experience
 * and the burner becomes SUPERHEATED (like a Blaze Cake). This runs before other mods,
 * so the Catalyst never feeds normal experience to the burner.
 * Create is called through reflection, so this mod still loads without Create.
 */
public class CatalystEvents {
    /** Liquid Experience used per superheat (millibuckets). */
    public static final int COST_MB = 1000;

    private static boolean resolved = false;
    private static Class<?> burnerBlockClass;
    private static Method tryInsert;

    private static void resolve() {
        if (resolved) {
            return;
        }
        resolved = true;
        try {
            burnerBlockClass = Class.forName("com.simibubi.create.content.processing.burner.BlazeBurnerBlock");
            tryInsert = burnerBlockClass.getMethod("tryInsert", BlockState.class, Level.class, BlockPos.class,
                    ItemStack.class, boolean.class, boolean.class, boolean.class);
        } catch (Throwable t) {
            QuantumAssembly.LOGGER.warn("Could not hook into Create's Blaze Burner: {}", t.toString());
            tryInsert = null;
        }
    }

    @SuppressWarnings("unchecked")
    private static InteractionResultHolder<ItemStack> insert(BlockState state, Level level, BlockPos pos,
                                                             ItemStack proxy, boolean simulate) throws Exception {
        return (InteractionResultHolder<ItemStack>) tryInsert.invoke(null, state, level, pos, proxy, true, false, simulate);
    }

    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (!held.is(ModItems.EXPERIENCE_CATALYST.get()) || !ModList.get().isLoaded("create")) {
            return;
        }
        resolve();
        if (tryInsert == null) {
            return;
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!burnerBlockClass.isInstance(state.getBlock())) {
            return;
        }

        boolean creative = event.getEntity().isCreative();
        if (!creative && ExperienceCatalystItem.stored(held) < COST_MB) {
            return; // not enough experience: leave it to other mods
        }

        // The Cake of Nebulae is registered as superheated fuel, so we use it as the "key".
        ItemStack proxy = new ItemStack(ModItems.CAKE_OF_NEBULAE.get());
        try {
            InteractionResultHolder<ItemStack> sim = insert(state, level, pos, proxy, true);
            // Cancel either way so normal experience is never poured in.
            event.setCanceled(true);
            if (!sim.getResult().consumesAction()) {
                event.setCancellationResult(InteractionResult.FAIL);
                return;
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            if (level.isClientSide) {
                return;
            }
            insert(state, level, pos, proxy, false);
            if (!creative) {
                SimpleFluidContent content = held.get(ModDataComponents.FLUID.get());
                FluidStack left = content.copy();
                left.shrink(COST_MB);
                if (left.isEmpty()) {
                    held.remove(ModDataComponents.FLUID.get());
                } else {
                    held.set(ModDataComponents.FLUID.get(), SimpleFluidContent.copyOf(left));
                }
            }
        } catch (Throwable t) {
            QuantumAssembly.LOGGER.warn("Superheating failed: {}", t.toString());
        }
    }
}
