package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The ritual lives in the Nebula Blaze Burner now: give it a full Experience Catalyst, a Nova Coin and a
 * Totem of Undying (right-click), light it with a Cake of Nebulae, and after a while it makes a Quantum Gate.
 */
public class NebulaBurnerBlockEntity extends BlockEntity {
    public static final int CATALYST = 0;
    public static final int COIN = 1;
    public static final int TOTEM = 2;
    private static final String[] KEYS = {"catalyst", "coin", "totem"};

    private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int progress = 0;
    /** Client only: the direction the head currently looks in (degrees) for the animation. */
    public float headAngle;
    public boolean headAngleReady;

    public NebulaBurnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NEBULA_BURNER.get(), pos, state);
    }

    public static int slotFor(ItemStack stack) {
        if (stack.is(ModItems.EXPERIENCE_CATALYST.get())) {
            return CATALYST;
        }
        if (stack.is(ModItems.NOVA_COIN.get())) {
            return COIN;
        }
        if (stack.is(Items.TOTEM_OF_UNDYING)) {
            return TOTEM;
        }
        return -1;
    }

    public static boolean catalystFull(ItemStack stack) {
        return ExperienceCatalystItem.stored(stack) >= Config.capacityMb();
    }

    public boolean allPresent() {
        return !items.get(CATALYST).isEmpty() && !items.get(COIN).isEmpty() && !items.get(TOTEM).isEmpty();
    }

    public String describe() {
        return "Burner: Experience Catalyst " + mark(CATALYST) + "  |  Nova Coin " + mark(COIN) + "  |  Totem of Undying " + mark(TOTEM);
    }

    private String mark(int slot) {
        return items.get(slot).isEmpty() ? "[ ]" : "[x]";
    }

    /** Tries to put one item in its slot. Returns the message to show the player. */
    public String tryInsert(int slot, ItemStack held, Player player) {
        if (!items.get(slot).isEmpty()) {
            return describe() + "  (that slot is already filled)";
        }
        if (slot == CATALYST && !catalystFull(held)) {
            return "The Experience Catalyst must be full. " + describe();
        }
        items.set(slot, held.copyWithCount(1));
        if (!player.isCreative()) {
            held.shrink(1);
        }
        changed();
        return describe();
    }

    public void giveBack(Player player) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                items.set(i, ItemStack.EMPTY);
            }
        }
        progress = 0;
        changed();
    }

    public void dropAll(Level level, BlockPos pos) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, stack);
                items.set(i, ItemStack.EMPTY);
            }
        }
        progress = 0;
        changed();
    }

    private void changed() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        if (!allPresent() || !NebulaBurnerBlock.isLit(state)) {
            if (progress != 0) {
                progress = 0;
                setChanged();
            }
            return;
        }
        progress++;
        if (progress == 1) {
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 0.5F);
        }
        if (progress % 2 == 0) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            level.sendParticles(ParticleTypes.PORTAL, x, y, z, 8, 0.35, 0.35, 0.35, 0.3);
            level.sendParticles(ParticleTypes.ENCHANT, x, y + 0.3, z, 4, 0.4, 0.4, 0.4, 0.5);
        }
        if (progress >= Config.ritualTicks()) {
            finish(level, pos, state);
        }
    }

    private void finish(ServerLevel level, BlockPos pos, BlockState state) {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        progress = 0;
        Block.popResource(level, pos.above(), new ItemStack(ModItems.QUANTUM_GATE.get()));
        level.setBlock(pos, state.setValue(NebulaBurnerBlock.LIT, false), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 0.7F, 1.2F);
        level.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 40, 0.4, 0.4, 0.4, 0.1);
        changed();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        for (int i = 0; i < items.size(); i++) {
            tag.put(KEYS[i], items.get(i).saveOptional(registries));
        }
        tag.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.parseOptional(registries, tag.getCompound(KEYS[i])));
        }
        progress = tag.getInt("progress");
    }
}
