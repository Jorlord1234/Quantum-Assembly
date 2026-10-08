package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

/** The Quantum Assembly Kit: a big storage block (more room than a backpack, stacks bigger than 64). */
public class KitBlockEntity extends BlockEntity {
    public static final int STORAGE_SLOTS = 54;

    /** The big storage: more room than a Toolbox and stacks bigger than 64. */
    private final ItemStackHandler storage = new ItemStackHandler(STORAGE_SLOTS) {
        @Override
        public int getSlotLimit(int slot) {
            return Config.kitStackLimit();
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return getSlotLimit(slot);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public KitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KIT.get(), pos, state);
    }

    public ItemStackHandler getStorage() {
        return storage;
    }

    /** Drops everything in the storage (used when the Kit is broken). */
    public void dropStorage(Level level, BlockPos pos) {
        for (int i = 0; i < storage.getSlots(); i++) {
            ItemStack stack = storage.getStackInSlot(i);
            while (!stack.isEmpty()) {
                int part = Math.min(stack.getCount(), Math.max(1, stack.getMaxStackSize()));
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copyWithCount(part));
                stack.shrink(part);
            }
            storage.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        // Stacks can be bigger than 99, so each stack is saved as "one item + a number".
        ListTag list = new ListTag();
        for (int i = 0; i < storage.getSlots(); i++) {
            ItemStack stack = storage.getStackInSlot(i);
            if (!stack.isEmpty()) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("slot", i);
                entry.putInt("n", stack.getCount());
                entry.put("item", stack.copyWithCount(1).save(registries));
                list.add(entry);
            }
        }
        tag.put("storage", list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < storage.getSlots(); i++) {
            storage.setStackInSlot(i, ItemStack.EMPTY);
        }
        ListTag list = tag.getList("storage", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            int slot = entry.getInt("slot");
            if (slot >= 0 && slot < storage.getSlots()) {
                ItemStack stack = ItemStack.parse(registries, entry.get("item")).orElse(ItemStack.EMPTY);
                if (!stack.isEmpty()) {
                    stack.setCount(Math.max(1, entry.getInt("n")));
                    storage.setStackInSlot(slot, stack);
                }
            }
        }
    }
}
