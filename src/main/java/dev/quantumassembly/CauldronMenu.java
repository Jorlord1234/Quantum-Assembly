package dev.quantumassembly;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.phys.Vec3;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;

/** Farmer's Delight's cooking pot screen, but it stays open on our golden Cauldron. */
public class CauldronMenu extends CookingPotMenu {
    private final CookingPotBlockEntity pot;

    public CauldronMenu(int windowId, Inventory inventory, CookingPotBlockEntity pot, ContainerData data) {
        super(windowId, inventory, pot, data);
        this.pot = pot;
    }

    @Override
    public boolean stillValid(Player player) {
        return !pot.isRemoved() && player.distanceToSqr(Vec3.atCenterOf(pot.getBlockPos())) <= 64.0;
    }
}
