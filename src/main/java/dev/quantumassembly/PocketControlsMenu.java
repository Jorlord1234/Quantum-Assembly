package dev.quantumassembly;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/** The upgrade screen of the Pocket Controls. It has no item slots, only 3 buttons. */
public class PocketControlsMenu extends AbstractContainerMenu {
    public static final int BUTTON_UPGRADE = 0;
    public static final int BUTTON_LINK = 1;
    public static final int BUTTON_DISASSEMBLE = 2;

    private final PocketControlsBlockEntity controls;
    private final ContainerData data;

    /** Client side. */
    public PocketControlsMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buf) {
        super(ModMenus.CONTROLS.get(), id);
        this.controls = null;
        this.data = new SimpleContainerData(3);
        addDataSlots(data);
    }

    /** Server side. */
    public PocketControlsMenu(int id, Inventory inventory, PocketControlsBlockEntity controls) {
        super(ModMenus.CONTROLS.get(), id);
        this.controls = controls;
        final ServerPlayer player = inventory.player instanceof ServerPlayer ? (ServerPlayer) inventory.player : null;
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                if (player == null) {
                    return 0;
                }
                PocketData.Plot plot = PocketData.get(player.getServer()).existing(controls.getOwner() != null ? controls.getOwner() : player.getUUID());
                int level = plot == null ? 0 : plot.level;
                if (index == 0) {
                    return level;
                }
                if (index == 1) {
                    return level >= PocketManager.RADIUS.length - 1 ? 0 : PocketManager.COST[level + 1];
                }
                return PocketManager.RADIUS[level] * 2 + 1;
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        addDataSlots(data);
    }

    public int level() {
        return data.get(0);
    }

    public int nextCost() {
        return data.get(1);
    }

    public int size() {
        return data.get(2);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (controls == null || !(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        if (id == BUTTON_UPGRADE) {
            PocketManager.upgrade(serverPlayer);
            return true;
        }
        if (id == BUTTON_LINK) {
            PocketManager.startLink(serverPlayer, controls.getBlockPos());
            serverPlayer.closeContainer();
            return true;
        }
        if (id == BUTTON_DISASSEMBLE) {
            Multiblock.disassemble(controls.getLevel(), controls.getBlockPos());
            serverPlayer.closeContainer();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (controls == null) {
            return true;
        }
        return !controls.isRemoved() && player.distanceToSqr(Vec3.atCenterOf(controls.getBlockPos())) <= 64.0;
    }
}
