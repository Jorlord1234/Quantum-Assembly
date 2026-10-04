package dev.quantumassembly;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/**
 * While a Nova Coin is in your inventory, Waystones teleports cost nothing (no XP, no items).
 * Uses reflection so the mod still loads without Waystones.
 */
public class WaystonesCompat {

    public static void register() {
        if (!ModList.get().isLoaded("waystones")) {
            return;
        }
        try {
            Class<?> balm = Class.forName("net.blay09.mods.balm.api.Balm");
            Object events = balm.getMethod("getEvents").invoke(null);
            Class<?> preEvent = Class.forName("net.blay09.mods.waystones.api.event.WaystoneTeleportEvent$Pre");
            Class<?> priorityClass = Class.forName("net.blay09.mods.balm.api.event.EventPriority");
            @SuppressWarnings({"unchecked", "rawtypes"})
            Object priority = Enum.valueOf((Class<Enum>) priorityClass.asSubclass(Enum.class), "Lowest");
            Class<?> requirementType = Class.forName("net.blay09.mods.waystones.api.requirement.WarpRequirement");

            Object freeRequirement = Proxy.newProxyInstance(requirementType.getClassLoader(),
                    new Class<?>[]{requirementType}, new FreeRequirement());

            Consumer<Object> handler = event -> {
                try {
                    Object context = event.getClass().getMethod("getContext").invoke(event);
                    Object entity = context.getClass().getMethod("getEntity").invoke(context);
                    if (entity instanceof Player player && hasNovaCoin(player)) {
                        context.getClass().getMethod("setRequirements", requirementType).invoke(context, freeRequirement);
                    }
                } catch (Throwable t) {
                    QuantumAssembly.LOGGER.warn("Nova Coin could not waive the Waystones cost: {}", t.toString());
                }
            };

            Method onEvent = Class.forName("net.blay09.mods.balm.api.event.BalmEvents")
                    .getMethod("onEvent", Class.class, Consumer.class, priorityClass);
            onEvent.invoke(events, preEvent, handler, priority);
            QuantumAssembly.LOGGER.info("Waystones found: the Nova Coin now makes teleports free.");
        } catch (Throwable t) {
            QuantumAssembly.LOGGER.warn("Could not hook into Waystones: {}", t.toString());
        }
    }

    private static boolean hasNovaCoin(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.NOVA_COIN.get())) {
                return true;
            }
        }
        return false;
    }

    /** A requirement that is always affordable and costs nothing. */
    private static class FreeRequirement implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            switch (method.getName()) {
                case "canAfford":
                case "isEmpty":
                    return true;
                case "toString":
                    return "NovaCoinFreeRequirement";
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "equals":
                    return proxy == args[0];
                default:
                    return null; // consume, rollback, appendHoverText: do nothing
            }
        }
    }
}
