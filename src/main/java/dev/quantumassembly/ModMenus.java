package dev.quantumassembly;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, QuantumAssembly.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<KitMenu>> KIT =
            MENUS.register("kit", () -> IMenuTypeExtension.create(KitMenu::new));
}
