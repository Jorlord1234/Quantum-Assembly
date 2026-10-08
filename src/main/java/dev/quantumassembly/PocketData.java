package dev.quantumassembly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

/** Remembers every owner's pocket space, and where every visitor has to go back to. */
public class PocketData extends SavedData {
    private static final String NAME = "quantum_assembly_pockets";

    public static class Plot {
        public int index;
        public int level;
        public int guestLevel;
        public int restLevel;
        public int vaultLevel;
        public boolean built;
        /** The Pocket Vault: up to 54 slots, how many are usable depends on vaultLevel. */
        public final ItemStack[] vault = new ItemStack[54];

        public Plot() {
            java.util.Arrays.fill(vault, ItemStack.EMPTY);
        }
    }

    public static class Return {
        public String dim = "";
        public double x;
        public double y;
        public double z;
        public float yaw;
    }

    private final Map<UUID, Plot> plots = new HashMap<>();
    private final Map<UUID, Return> returns = new HashMap<>();
    private int next = 0;

    public static PocketData get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(PocketData::new, PocketData::load, null), NAME);
    }

    public Plot plot(UUID id) {
        return plots.computeIfAbsent(id, key -> {
            Plot plot = new Plot();
            plot.index = next++;
            setDirty();
            return plot;
        });
    }

    public Plot existing(UUID id) {
        return plots.get(id);
    }

    /** The plot with this index (they sit next to each other in the pocket dimension), or null. */
    public Plot byIndex(int index) {
        for (Plot plot : plots.values()) {
            if (plot.index == index) {
                return plot;
            }
        }
        return null;
    }

    public void setReturn(UUID visitor, Return value) {
        returns.put(visitor, value);
        setDirty();
    }

    public Return getReturn(UUID visitor) {
        return returns.get(visitor);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("next", next);
        ListTag list = new ListTag();
        for (Map.Entry<UUID, Plot> entry : plots.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putUUID("owner", entry.getKey());
            t.putInt("index", entry.getValue().index);
            t.putInt("level", entry.getValue().level);
            t.putInt("guestLevel", entry.getValue().guestLevel);
            t.putInt("restLevel", entry.getValue().restLevel);
            t.putInt("vaultLevel", entry.getValue().vaultLevel);
            ListTag vault = new ListTag();
            for (int i = 0; i < entry.getValue().vault.length; i++) {
                ItemStack stack = entry.getValue().vault[i];
                if (!stack.isEmpty()) {
                    CompoundTag e = new CompoundTag();
                    e.putInt("slot", i);
                    e.putInt("n", stack.getCount());
                    e.put("item", stack.copyWithCount(1).save(registries));
                    vault.add(e);
                }
            }
            t.put("vault", vault);
            t.putBoolean("built", entry.getValue().built);
            list.add(t);
        }
        tag.put("plots", list);
        ListTag rets = new ListTag();
        for (Map.Entry<UUID, Return> entry : returns.entrySet()) {
            CompoundTag t = new CompoundTag();
            Return r = entry.getValue();
            t.putUUID("who", entry.getKey());
            t.putString("dim", r.dim);
            t.putDouble("x", r.x);
            t.putDouble("y", r.y);
            t.putDouble("z", r.z);
            t.putFloat("yaw", r.yaw);
            rets.add(t);
        }
        tag.put("returns", rets);
        return tag;
    }

    public static PocketData load(CompoundTag tag, HolderLookup.Provider registries) {
        PocketData data = new PocketData();
        data.next = tag.getInt("next");
        ListTag list = tag.getList("plots", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag t = list.getCompound(i);
            Plot p = new Plot();
            p.index = t.getInt("index");
            p.level = t.getInt("level");
            p.guestLevel = t.getInt("guestLevel");
            p.restLevel = t.getInt("restLevel");
            p.vaultLevel = t.getInt("vaultLevel");
            ListTag vault = t.getList("vault", Tag.TAG_COMPOUND);
            for (int v = 0; v < vault.size(); v++) {
                CompoundTag e = vault.getCompound(v);
                int slot = e.getInt("slot");
                if (slot >= 0 && slot < p.vault.length) {
                    ItemStack stack = ItemStack.parse(registries, e.get("item")).orElse(ItemStack.EMPTY);
                    if (!stack.isEmpty()) {
                        stack.setCount(Math.max(1, e.getInt("n")));
                        p.vault[slot] = stack;
                    }
                }
            }
            p.built = t.getBoolean("built");
            data.plots.put(t.getUUID("owner"), p);
        }
        ListTag rets = tag.getList("returns", Tag.TAG_COMPOUND);
        for (int i = 0; i < rets.size(); i++) {
            CompoundTag t = rets.getCompound(i);
            Return r = new Return();
            r.dim = t.getString("dim");
            r.x = t.getDouble("x");
            r.y = t.getDouble("y");
            r.z = t.getDouble("z");
            r.yaw = t.getFloat("yaw");
            data.returns.put(t.getUUID("who"), r);
        }
        return data;
    }
}
