package dev.quantumassembly;

import java.util.function.Supplier;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, QuantumAssembly.MODID);

    /** Stores the liquid inside the Experience Catalyst. */
    public static final Supplier<DataComponentType<SimpleFluidContent>> FLUID = COMPONENTS.register("fluid",
            () -> DataComponentType.<SimpleFluidContent>builder()
                    .persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
                    .cacheEncoding()
                    .build());
}
