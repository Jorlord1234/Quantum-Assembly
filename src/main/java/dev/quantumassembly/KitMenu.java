package dev.quantumassembly;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.SlotItemHandler;

/** The screen of the Quantum Assembly Kit: 3 ritual slots on top, then 36 big storage slots. */
public class KitMenu extends AbstractContainerMenu {
    private static final int RITUAL = 3;
    private static final int STORAGE = KitBlockEntity.STORAGE_SLOTS;
    private final KitBlockEntity kit;

    /** Client side: the block position is sent by the server. */
    public KitMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(id, inventory, (KitBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public KitMenu(int id, Inventory inventory, KitBlockEntity kit) {
        super(ModMenus.KIT.get(), id);
        if (kit == null) {
            throw new IllegalStateException("The Quantum Assembly Kit is missing");
        }
        this.kit = kit;
        // The 3 ritual slots sit in a column on the left, like upgrade tabs.
        for (int i = 0; i < RITUAL; i++) {
            addSlot(new SlotItemHandler(kit.getRitual(), i, 10, 24 + i * 28));
        }
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new SlotItemHandler(kit.getStorage(), col + row * 9, 40 + col * 18, 20 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 40 + col * 18, 148 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 40 + col * 18, 206));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return !kit.isRemoved() && player.distanceToSqr(Vec3.atCenterOf(kit.getBlockPos())) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        int kitSlots = RITUAL + STORAGE;
        if (index < kitSlots) {
            if (!moveItemStackTo(stack, kitSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean moved = false;
            int ritualSlot = KitBlockEntity.slotFor(stack);
            if (ritualSlot >= 0 && slots.get(ritualSlot).mayPlace(stack)) {
                moved = moveItemStackTo(stack, ritualSlot, ritualSlot + 1, false);
            }
            if (!moved && !moveItemStackTo(stack, RITUAL, kitSlots, false)) {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }
}
