package dev.quantumassembly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

/** Remembers every player's personal pocket space. */
public class PocketData extends SavedData {
    private static final String NAME = "quantum_assembly_pockets";

    public static class Plot {
        public int index;
        public int level;
        public boolean built;
        public String returnDim = "";
        public double rx;
        public double ry;
        public double rz;
        public float yaw;
    }

    private final Map<UUID, Plot> plots = new HashMap<>();
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

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("next", next);
        ListTag list = new ListTag();
        for (Map.Entry<UUID, Plot> entry : plots.entrySet()) {
            Plot p = entry.getValue();
            CompoundTag t = new CompoundTag();
            t.putUUID("owner", entry.getKey());
            t.putInt("index", p.index);
            t.putInt("level", p.level);
            t.putBoolean("built", p.built);
            t.putString("dim", p.returnDim);
            t.putDouble("rx", p.rx);
            t.putDouble("ry", p.ry);
            t.putDouble("rz", p.rz);
            t.putFloat("yaw", p.yaw);
            list.add(t);
        }
        tag.put("plots", list);
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
            p.built = t.getBoolean("built");
            p.returnDim = t.getString("dim");
            p.rx = t.getDouble("rx");
            p.ry = t.getDouble("ry");
            p.rz = t.getDouble("rz");
            p.yaw = t.getFloat("yaw");
            data.plots.put(t.getUUID("owner"), p);
        }
        return data;
    }
}
