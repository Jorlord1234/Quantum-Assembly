package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * Right-click a Create Blaze Burner with an Experience Catalyst: the Catalyst pays 1 bucket of its
 * Liquid Experience and the burner receives SUPER (hyper) Liquid Experience instead.
 * The Catalyst itself is never used up. Normal experience is never poured into the burner.
 */
public class CatalystEvents {
    public static final int COST_MB = 1000;

    private static Fluid superFluid;
    private static boolean searched = false;

    /** Finds the Super/Hyper Liquid Experience fluid of Create: Enchantment Industry (any version). */
    private static Fluid findSuperFluid() {
        if (!searched || superFluid == null) {
            searched = true;
            ResourceLocation preferred = ResourceLocation.fromNamespaceAndPath("create_enchantment_industry", "hyper_experience");
            superFluid = BuiltInRegistries.FLUID.getOptional(preferred).filter(f -> !f.defaultFluidState().isEmpty()).orElse(null);
            if (superFluid == null) {
                for (ResourceLocation id : BuiltInRegistries.FLUID.keySet()) {
                    String path = id.getPath();
                    if (!path.contains("flowing") && path.contains("experience")
                            && (path.contains("hyper") || path.contains("super"))) {
                        superFluid = BuiltInRegistries.FLUID.get(id);
                        break;
                    }
                }
            }
        }
        return superFluid;
    }

    private static IFluidHandler burnerHandler(Level level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        IFluidHandler h = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, state, be, null);
        if (h != null) {
            return h;
        }
        for (Direction d : Direction.values()) {
            h = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, state, be, d);
            if (h != null) {
                return h;
            }
        }
        return null;
    }

    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (!held.is(ModItems.EXPERIENCE_CATALYST.get()) || !ModList.get().isLoaded("create")) {
            return;
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String ns = blockId.getNamespace();
        boolean createFamily = ns.equals("create") || ns.equals("create_enchantment_industry") || ns.equals("create_dragons_plus");
        if (!createFamily || !blockId.getPath().contains("blaze")) {
            return; // only Blaze Burners / Blaze Enchanters / Workstations
        }

        boolean creative = event.getEntity().isCreative();
        if (!creative && ExperienceCatalystItem.stored(held) < COST_MB) {
            return; // too little experience: leave it to other mods
        }

        // From here on we always cancel, so the Catalyst can never pour normal experience in.
        event.setCanceled(true);
        Fluid fluid = findSuperFluid();
        IFluidHandler handler = fluid == null ? null : burnerHandler(level, pos, state);
        if (handler == null) {
            event.setCancellationResult(InteractionResult.FAIL);
            return;
        }
        int accepted = handler.fill(new FluidStack(fluid, COST_MB), IFluidHandler.FluidAction.SIMULATE);
        if (accepted <= 0) {
            event.setCancellationResult(InteractionResult.FAIL);
            return;
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (level.isClientSide) {
            return;
        }
        int filled = handler.fill(new FluidStack(fluid, accepted), IFluidHandler.FluidAction.EXECUTE);
        if (filled > 0 && !creative) {
            SimpleFluidContent content = held.get(ModDataComponents.FLUID.get());
            FluidStack left = content.copy();
            left.shrink(filled);
            if (left.isEmpty()) {
                held.remove(ModDataComponents.FLUID.get());
            } else {
                held.set(ModDataComponents.FLUID.get(), SimpleFluidContent.copyOf(left));
            }
        }
    }
}
