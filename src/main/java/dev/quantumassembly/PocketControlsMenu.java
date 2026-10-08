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

/** The screen of the Pocket Controls. It has no item slots: upgrade cards and a few buttons. */
public class PocketControlsMenu extends AbstractContainerMenu {
    public static final int BUTTON_UPGRADE = 0;
    public static final int BUTTON_LINK = 1;
    public static final int BUTTON_DISASSEMBLE = 2;
    public static final int BUTTON_GUESTS = 3;
    public static final int BUTTON_REST = 4;
    public static final int BUTTON_VAULT_UPGRADE = 5;
    public static final int BUTTON_VAULT_OPEN = 6;
    private static final int DATA_COUNT = 11;

    private final PocketControlsBlockEntity controls;
    private final ContainerData data;

    /** Client side. */
    public PocketControlsMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buf) {
        super(ModMenus.CONTROLS.get(), id);
        this.controls = null;
        this.data = new SimpleContainerData(DATA_COUNT);
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
                int guests = plot == null ? 0 : plot.guestLevel;
                int rest = plot == null ? 0 : plot.restLevel;
                int vault = plot == null ? 0 : plot.vaultLevel;
                switch (index) {
                    case 0:
                        return level;
                    case 1:
                        return level >= PocketManager.RADIUS.length - 1 ? 0 : PocketManager.COST[level + 1];
                    case 2:
                        return PocketManager.RADIUS[level] * 2 + 1;
                    case 3:
                        return guests;
                    case 4:
                        return guests >= PocketManager.GUEST_MAX.length - 1 ? 0 : PocketManager.GUEST_COST[guests + 1];
                    case 5:
                        return PocketManager.GUEST_MAX[guests];
                    case 6:
                        return rest;
                    case 7:
                        return rest >= PocketManager.REST_COST.length - 1 ? 0 : PocketManager.REST_COST[rest + 1];
                    case 8:
                        return vault;
                    case 9:
                        return vault >= PocketManager.VAULT_COST.length - 1 ? 0 : PocketManager.VAULT_COST[vault + 1];
                    default:
                        return PocketManager.VAULT_ROWS[vault] * 9;
                }
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
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

    public int guestLevel() {
        return data.get(3);
    }

    public int guestNextCost() {
        return data.get(4);
    }

    public int guestMax() {
        return data.get(5);
    }

    public int restLevel() {
        return data.get(6);
    }

    public int restNextCost() {
        return data.get(7);
    }

    public int vaultLevel() {
        return data.get(8);
    }

    public int vaultNextCost() {
        return data.get(9);
    }

    public int vaultSlots() {
        return data.get(10);
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
        if (id == BUTTON_GUESTS) {
            PocketManager.upgradeGuests(serverPlayer);
            return true;
        }
        if (id == BUTTON_REST) {
            PocketManager.upgradeRest(serverPlayer);
            return true;
        }
        if (id == BUTTON_VAULT_UPGRADE) {
            PocketManager.upgradeVault(serverPlayer);
            return true;
        }
        if (id == BUTTON_VAULT_OPEN) {
            PocketManager.openVault(serverPlayer);
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
