package dev.quantumassembly;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

@Mod(QuantumAssembly.MODID)
public class QuantumAssembly {
    public static final String MODID = "quantum_assembly";

    public QuantumAssembly(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModDataComponents.COMPONENTS.register(modEventBus);
        ModTabs.TABS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /** True only for Liquid Experience from Create: Enchantment Industry. */
    public static boolean isExperience(FluidStack stack) {
        ResourceLocation id = BuiltInRegistries.FLUID.getKey(stack.getFluid());
        return id.getNamespace().equals("create_enchantment_industry") && id.getPath().equals("experience");
    }

    // Makes the Experience Catalyst work like a tank, so a Create Spout can fill it.
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (stack, context) -> new FluidHandlerItemStack(ModDataComponents.FLUID, stack, Config.capacityMb()) {
                    @Override
                    public boolean canFillFluidType(FluidStack fluid) {
                        return isExperience(fluid);
                    }
                },
                ModItems.EXPERIENCE_CATALYST.get());
    }
}
