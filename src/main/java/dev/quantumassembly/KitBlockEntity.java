package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * The Quantum Assembly Kit: 54 big storage slots, 4 slots for Sophisticated Backpacks upgrades, and a lid that swings
 * open while somebody has it open (like a Create toolbox).
 *
 * Upgrades: any Sophisticated Backpacks upgrade fits. The Stack upgrades work here (Tier 1 to 4 and Omega multiply the
 * size of every slot, and several of them multiply together); the other upgrades are stored but have no effect yet.
 */
public class KitBlockEntity extends BlockEntity {
    public static final int STORAGE_SLOTS = 54;
    public static final int UPGRADE_SLOTS = 4;
    private static final int MAX_SLOT_LIMIT = 65536;
    private static final String BACKPACKS = "sophisticatedbackpacks";

    private final ItemStackHandler upgrades = new ItemStackHandler(UPGRADE_SLOTS) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isBackpackUpgrade(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            KitBlockEntity.this.setChanged();
        }
    };

    private final ItemStackHandler storage = new ItemStackHandler(STORAGE_SLOTS) {
        @Override
        public int getSlotLimit(int slot) {
            return slotLimit();
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return slotLimit();
        }

        @Override
        protected void onContentsChanged(int slot) {
            KitBlockEntity.this.setChanged();
        }
    };

    /** How many players have the Kit open (server), and how far the lid is open (client: 0 = closed, 1 = open). */
    private int openers;
    public float lid;
    public float lastLid;

    public KitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KIT.get(), pos, state);
    }

    public ItemStackHandler getStorage() {
        return storage;
    }

    public ItemStackHandler getUpgrades() {
        return upgrades;
    }

    // ---------- Sophisticated Backpacks upgrades ----------

    /** Any item from Sophisticated Backpacks that is an upgrade ("..._upgrade", or the "stack_upgrade_..." ones). */
    public static boolean isBackpackUpgrade(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!BACKPACKS.equals(id.getNamespace())) {
            return false;
        }
        String path = id.getPath();
        return path.endsWith("_upgrade") || path.startsWith("stack_upgrade_");
    }

    /** What one upgrade does to the size of every slot (1 = nothing). */
    private static int multiplierOf(ItemStack stack) {
        if (!isBackpackUpgrade(stack)) {
            return 1;
        }
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        if (path.startsWith("stack_upgrade_tier_")) {
            try {
                int tier = Integer.parseInt(path.substring("stack_upgrade_tier_".length()));
                return 1 << Math.max(1, Math.min(tier, 4));   // tier 1 = x2, 2 = x4, 3 = x8, 4 = x16
            } catch (NumberFormatException ignored) {
                return 1;
            }
        }
        if (path.startsWith("stack_upgrade_omega")) {
            return 32;
        }
        return 1;
    }

    public int stackMultiplier() {
        long multiplier = 1;
        for (int i = 0; i < upgrades.getSlots(); i++) {
            multiplier *= multiplierOf(upgrades.getStackInSlot(i));
        }
        return (int) Math.min(multiplier, MAX_SLOT_LIMIT);
    }

    /** How many items fit in one storage slot: the config value times the Stack upgrades. */
    public int slotLimit() {
        long limit = (long) Config.kitStackLimit() * stackMultiplier();
        return (int) Math.min(limit, MAX_SLOT_LIMIT);
    }

    // ---------- lid ----------

    public void startOpen(Player player) {
        if (level == null || level.isClientSide || isRemoved() || player.isSpectator()) {
            return;
        }
        if (openers++ == 0) {
            level.playSound(null, worldPosition, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.6F, level.random.nextFloat() * 0.1F + 0.9F);
        }
        signalOpeners();
    }

    public void stopOpen(Player player) {
        if (level == null || level.isClientSide || isRemoved() || player.isSpectator() || openers <= 0) {
            return;
        }
        if (--openers == 0) {
            level.playSound(null, worldPosition, SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS, 0.6F, level.random.nextFloat() * 0.1F + 0.9F);
        }
        signalOpeners();
    }

    private void signalOpeners() {
        if (level != null) {
            level.blockEvent(worldPosition, getBlockState().getBlock(), 1, openers);
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            openers = type;
            return true;
        }
        return super.triggerEvent(id, type);
    }

    /** Client side: move the lid a little every tick. */
    public static void clientTick(Level level, BlockPos pos, BlockState state, KitBlockEntity kit) {
        kit.lastLid = kit.lid;
        kit.lid = Mth.clamp(kit.lid + (kit.openers > 0 ? 0.1F : -0.1F), 0.0F, 1.0F);
    }

    // ---------- items ----------

    public void dropStorage(Level level, BlockPos pos) {
        drop(level, pos, storage);
        drop(level, pos, upgrades);
    }

    private static void drop(Level level, BlockPos pos, ItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            while (!stack.isEmpty()) {
                int part = Math.min(stack.getCount(), Math.max(1, stack.getMaxStackSize()));
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copyWithCount(part));
                stack.shrink(part);
            }
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    private static ListTag save(ItemStackHandler handler, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            CompoundTag entry = new CompoundTag();
            entry.putInt("slot", i);
            entry.putInt("n", stack.getCount());
            entry.put("item", stack.copyWithCount(1).save(registries));
            list.add(entry);
        }
        return list;
    }

    private static void load(ItemStackHandler handler, ListTag list, HolderLookup.Provider registries) {
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            int slot = entry.getInt("slot");
            if (slot < 0 || slot >= handler.getSlots()) {
                continue;
            }
            ItemStack stack = ItemStack.parse(registries, entry.get("item")).orElse(ItemStack.EMPTY);
            if (stack.isEmpty()) {
                continue;
            }
            stack.setCount(Math.max(1, entry.getInt("n")));
            handler.setStackInSlot(slot, stack);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("storage", save(storage, registries));
        tag.put("upgrades", save(upgrades, registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        load(upgrades, tag.getList("upgrades", Tag.TAG_COMPOUND), registries);
        load(storage, tag.getList("storage", Tag.TAG_COMPOUND), registries);
    }
}
