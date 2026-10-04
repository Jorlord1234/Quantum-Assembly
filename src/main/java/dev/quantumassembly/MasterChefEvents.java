package dev.quantumassembly;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

/** Earning the "Master Chef" advancement gives the Master Chef's Cauldron and the Chef's Hat. */
public class MasterChefEvents {

    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!matches(event.getAdvancement())) {
            return;
        }
        give(player, new ItemStack(ModItems.MASTER_CHEFS_CAULDRON.get()));
        give(player, new ItemStack(ModItems.CHEFS_HAT.get()));
    }

    private static boolean matches(AdvancementHolder holder) {
        String id = holder.id().toString();
        String configured = Config.masterChefAdvancement().trim();
        if (!configured.isEmpty() && configured.equals(id)) {
            return true;
        }
        String path = holder.id().getPath().toLowerCase();
        if (path.contains("master_chef") || path.contains("masterchef") || path.contains("master-chef")) {
            return true;
        }
        return holder.value().display()
                .map(d -> d.getTitle().getString().equalsIgnoreCase("Master Chef"))
                .orElse(false);
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
