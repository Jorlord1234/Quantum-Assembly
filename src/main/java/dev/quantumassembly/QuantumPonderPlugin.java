package dev.quantumassembly;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

/**
 * Adds Create's "Ponder" (hold W over an item) for the pocket parts. The structures live in
 * assets/quantum_assembly/ponder/*.nbt and the texts in the lang file under quantum_assembly.ponder.<scene>.<key>.
 */
public class QuantumPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return QuantumAssembly.MODID;
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(QuantumAssembly.MODID, path);
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(id("pocket_casing"), id("pocket_core"), id("pocket_controls"))
                .addStoryBoard("pocket_multiblock", QuantumPonderScenes::multiblock);
        helper.forComponents(id("kinetic_port"), id("pocket_output"))
                .addStoryBoard("pocket_power", QuantumPonderScenes::power);
    }
}
