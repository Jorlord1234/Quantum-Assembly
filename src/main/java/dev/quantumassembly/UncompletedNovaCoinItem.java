package dev.quantumassembly;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Thrown into the void, this coin is finished into a Nova Coin and returns to the nearest player. */
public class UncompletedNovaCoinItem extends Item {
    public UncompletedNovaCoinItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Level level = entity.level();
        if (level.isClientSide || entity.getY() >= level.getMinBuildHeight() - 4) {
            return false;
        }
        Player player = level.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 512.0, false);
        if (player == null) {
            return false; // nobody around: behaves like a normal item
        }
        ServerLevel server = (ServerLevel) level;
        server.sendParticles(ParticleTypes.PORTAL, entity.getX(), entity.getY() + 2, entity.getZ(), 40, 0.3, 0.3, 0.3, 0.5);
        entity.setItem(new ItemStack(ModItems.NOVA_COIN.get(), stack.getCount()));
        entity.setPos(player.getX(), player.getY() + 1.0, player.getZ());
        entity.setDeltaMovement(0.0, 0.25, 0.0);
        entity.setDefaultPickUpDelay();
        entity.hurtMarked = true;
        server.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(), 25, 0.3, 0.3, 0.3, 0.05);
        return true;
    }
}
