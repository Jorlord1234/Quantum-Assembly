package dev.quantumassembly;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = QuantumAssembly.MODID)
public class PocketEvents {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.tickCount % 20 == 0
                && player.level().dimension().equals(PocketManager.POCKET)
                && player.getY() < 40) {
            PocketManager.rescue(player);
        }
    }
}
