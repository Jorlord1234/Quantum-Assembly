package dev.quantumassembly;

import java.lang.reflect.Method;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

/**
 * Right-click a burner from Create: Enchantment Industry (Blaze Enchanter, Blaze Forger...) with an
 * Experience Catalyst. It spends 1 bucket and puts "super" (special) experience into the burner.
 * The Catalyst is not used up. Normal Create burners are never touched.
 */
@EventBusSubscriber(modid = QuantumAssembly.MODID)
public class CatalystEvents {
    private static final String BURNER_CLASS =
            "plus.dragons.createenchantmentindustry.common.fluids.experience.BlazeExperienceBlock";
    private static final int COST_MB = 1000;

    private static Method applyFuel;
    private static Class<?> burnerClass;
    private static boolean lookedUp;

    private static void lookUp() {
        if (lookedUp) {
            return;
        }
        lookedUp = true;
        try {
            burnerClass = Class.forName(BURNER_CLASS);
            applyFuel = burnerClass.getMethod("applyFuel", BlockState.class, Level.class, BlockPos.class,
                    ItemStack.class, boolean.class, boolean.class, boolean.class);
        } catch (ReflectiveOperationException | LinkageError e) {
            QuantumAssembly.LOGGER.warn("Create: Enchantment Industry burner hook not found, the Catalyst will not work on burners.", e);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (!(held.getItem() instanceof ExperienceCatalystItem)) {
            return;
        }
        lookUp();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (burnerClass == null || applyFuel == null || !burnerClass.isInstance(state.getBlock())) {
            return; // not an Enchantment Industry burner: do nothing special
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (level.isClientSide) {
            return;
        }
        Player player = event.getEntity();
        SimpleFluidContent content = held.get(ModDataComponents.FLUID.get());
        int stored = content == null ? 0 : content.copy().getAmount();
        if (stored < COST_MB) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.catalyst_empty"), true);
            return;
        }
        ItemStack charge = new ItemStack(ModItems.SUPER_EXPERIENCE_CHARGE.get());
        try {
            // First ask the burner if it would accept the experience, then really add it.
            InteractionResultHolder<?> test = (InteractionResultHolder<?>) applyFuel.invoke(null, state, level, pos, charge.copy(), true, true, true);
            if (!test.getResult().consumesAction()) {
                player.displayClientMessage(Component.translatable("message.quantum_assembly.burner_full"), true);
                return;
            }
            InteractionResultHolder<?> done = (InteractionResultHolder<?>) applyFuel.invoke(null, state, level, pos, charge, true, true, false);
            if (!done.getResult().consumesAction()) {
                player.displayClientMessage(Component.translatable("message.quantum_assembly.burner_full"), true);
                return;
            }
        } catch (ReflectiveOperationException e) {
            QuantumAssembly.LOGGER.warn("Could not give super experience to the burner.", e);
            return;
        }
        FluidStack left = content.copy();
        left.shrink(COST_MB);
        held.set(ModDataComponents.FLUID.get(), SimpleFluidContent.copyOf(left));
        player.displayClientMessage(Component.translatable("message.quantum_assembly.burner_charged"), true);
    }
}
