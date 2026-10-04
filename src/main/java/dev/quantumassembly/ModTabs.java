package dev.quantumassembly;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, QuantumAssembly.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.quantum_assembly"))
                    .icon(() -> ModItems.EXPERIENCE_CATALYST.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.EXPERIENCE_CATALYST.get());
                        // A full Catalyst for testing (only if Create: Enchantment Industry is installed).
                        BuiltInRegistries.FLUID
                                .getOptional(ResourceLocation.fromNamespaceAndPath("create_enchantment_industry", "experience"))
                                .ifPresent(fluid -> {
                                    ItemStack full = new ItemStack(ModItems.EXPERIENCE_CATALYST.get());
                                    full.set(ModDataComponents.FLUID.get(),
                                            SimpleFluidContent.copyOf(new FluidStack(fluid, Config.capacityMb())));
                                    output.accept(full);
                                });
                        output.accept(ModItems.INCOMPLETE_EXPERIENCE_CATALYST.get());
                        output.accept(ModItems.NOVA_TEMPLATE.get());
                        output.accept(ModItems.UNCOMPLETED_NOVA_COIN.get());
                        output.accept(ModItems.NOVA_COIN.get());
                        output.accept(ModItems.CAKE_OF_NEBULAE.get());
                        output.accept(ModItems.CHEFS_HAT.get());
                        output.accept(ModItems.STARGLASS.get());
                        output.accept(ModItems.STARGLASS_ORE.get());
                        output.accept(ModItems.QUANTUM_ASSEMBLY_KIT.get());
                        output.accept(ModItems.MASTER_CHEFS_CAULDRON.get());
                    })
                    .build());
}
