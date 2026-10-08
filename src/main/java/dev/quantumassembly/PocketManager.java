package dev.quantumassembly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Everything about personal pocket spaces: building them, going in, coming out, upgrading. */
public class PocketManager {
    public static final ResourceKey<Level> POCKET =
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(QuantumAssembly.MODID, "pocket"));

    /** Every player's space is this far apart, so they never touch. */
    private static final int SPACING = 512;
    private static final int FLOOR_Y = 64;
    private static final int WALL_HEIGHT = 40;
    /** Half-size of the floor for level 0 to 5. */
    public static final int[] RADIUS = {8, 12, 16, 24, 32, 48};
    /** Starglass needed to reach level 1 to 5. */
    public static final int[] COST = {0, 8, 16, 32, 64, 128};
    /** How many guests (other players) can be inside your space at the same time, level 0 to 4. */
    public static final int[] GUEST_MAX = {1, 2, 4, 8, 16};
    /** Starglass needed to reach guest level 1 to 4. */
    public static final int[] GUEST_COST = {0, 4, 8, 16, 32};

    public static BlockPos center(PocketData.Plot plot) {
        return new BlockPos(plot.index * SPACING, FLOOR_Y, 0);
    }

    /** A player pressed "Link a Gate" and now has 30 seconds to right-click a Gate. */
    public record Pending(String dim, BlockPos pos, long expires) {
    }

    private static final Map<UUID, Pending> PENDING = new HashMap<>();

    public static void startLink(ServerPlayer player, BlockPos controlsPos) {
        PENDING.put(player.getUUID(), new Pending(player.level().dimension().location().toString(), controlsPos,
                player.level().getGameTime() + 600));
        player.displayClientMessage(Component.translatable("message.quantum_assembly.link_armed"), true);
    }

    /** Returns the waiting link request (and removes it), or null. */
    public static Pending takePending(ServerPlayer player) {
        Pending p = PENDING.remove(player.getUUID());
        if (p == null || p.expires() < player.level().getGameTime()) {
            return null;
        }
        return p;
    }

    /** Go into the pocket space that belongs to "owner". */
    public static void enter(ServerPlayer player, UUID owner) {
        MinecraftServer server = player.getServer();
        ServerLevel pocket = server.getLevel(POCKET);
        if (pocket == null) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.no_pocket"), true);
            return;
        }
        PocketData data = PocketData.get(server);
        PocketData.Plot plot = data.plot(owner);
        if (!player.getUUID().equals(owner)) {
            int inside = 0;
            for (ServerPlayer other : pocket.players()) {
                if (!other.getUUID().equals(owner) && !other.getUUID().equals(player.getUUID())
                        && Math.round((float) other.getX() / SPACING) == plot.index) {
                    inside++;
                }
            }
            if (inside >= GUEST_MAX[plot.guestLevel]) {
                player.displayClientMessage(Component.translatable("message.quantum_assembly.pocket_full", GUEST_MAX[plot.guestLevel]), true);
                return;
            }
        }
        if (!plot.built) {
            build(pocket, plot, -1);
            plot.built = true;
        }
        PocketData.Return back = new PocketData.Return();
        back.dim = player.level().dimension().location().toString();
        back.x = player.getX();
        back.y = player.getY();
        back.z = player.getZ();
        back.yaw = player.getYRot();
        data.setReturn(player.getUUID(), back);
        data.setDirty();
        BlockPos c = center(plot);
        player.teleportTo(pocket, c.getX() + 0.5, FLOOR_Y + 1.0, c.getZ() + 0.5, 0.0F, 0.0F);
    }

    public static void leave(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        PocketData.Return back = PocketData.get(server).getReturn(player.getUUID());
        ServerLevel dest = null;
        if (back != null && !back.dim.isEmpty()) {
            dest = server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(back.dim)));
        }
        if (dest == null) {
            ServerLevel overworld = server.overworld();
            BlockPos spawn = overworld.getSharedSpawnPos();
            player.teleportTo(overworld, spawn.getX() + 0.5, spawn.getY() + 1.0, spawn.getZ() + 0.5, 0.0F, 0.0F);
            return;
        }
        player.teleportTo(dest, back.x, back.y, back.z, back.yaw, 0.0F);
    }

    /** Spend Starglass to make your space bigger. */
    public static void upgrade(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        ServerLevel pocket = server.getLevel(POCKET);
        PocketData data = PocketData.get(server);
        PocketData.Plot plot = data.plot(player.getUUID());
        if (plot.level >= RADIUS.length - 1) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.max_level"), true);
            return;
        }
        int cost = COST[plot.level + 1];
        int have = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.STARGLASS.get())) {
                have += stack.getCount();
            }
        }
        if (have < cost) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.need_starglass", cost, have), true);
            return;
        }
        if (!player.isCreative()) {
            int left = cost;
            for (ItemStack stack : player.getInventory().items) {
                if (left > 0 && stack.is(ModItems.STARGLASS.get())) {
                    int take = Math.min(left, stack.getCount());
                    stack.shrink(take);
                    left -= take;
                }
            }
        }
        int oldRadius = RADIUS[plot.level];
        plot.level++;
        if (pocket != null && plot.built) {
            build(pocket, plot, oldRadius);
        }
        data.setDirty();
        player.displayClientMessage(Component.translatable("message.quantum_assembly.upgraded", plot.level, RADIUS[plot.level] * 2 + 1), true);
    }

    /** Spend Starglass to let more guests in at the same time. */
    public static void upgradeGuests(ServerPlayer player) {
        PocketData data = PocketData.get(player.getServer());
        PocketData.Plot plot = data.plot(player.getUUID());
        if (plot.guestLevel >= GUEST_MAX.length - 1) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.max_guests"), true);
            return;
        }
        int cost = GUEST_COST[plot.guestLevel + 1];
        int have = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.STARGLASS.get())) {
                have += stack.getCount();
            }
        }
        if (have < cost) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.need_starglass", cost, have), true);
            return;
        }
        if (!player.isCreative()) {
            int left = cost;
            for (ItemStack stack : player.getInventory().items) {
                if (left > 0 && stack.is(ModItems.STARGLASS.get())) {
                    int take = Math.min(left, stack.getCount());
                    stack.shrink(take);
                    left -= take;
                }
            }
        }
        plot.guestLevel++;
        data.setDirty();
        player.displayClientMessage(Component.translatable("message.quantum_assembly.guests_upgraded", plot.guestLevel, GUEST_MAX[plot.guestLevel]), true);
    }

    /** Builds (or rebuilds bigger) a player's floor and invisible walls. */
    private static void build(ServerLevel pocket, PocketData.Plot plot, int oldRadius) {
        BlockPos c = center(plot);
        int r = RADIUS[plot.level];
        if (oldRadius >= 0) {
            cage(pocket, c, oldRadius, Blocks.AIR.defaultBlockState());
        }
        BlockState dark = Blocks.POLISHED_DEEPSLATE.defaultBlockState();
        BlockState light = Blocks.DEEPSLATE_TILES.defaultBlockState();
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                pocket.setBlock(m.set(c.getX() + dx, FLOOR_Y, c.getZ() + dz), ((dx + dz) & 1) == 0 ? dark : light, Block.UPDATE_CLIENTS);
            }
        }
        cage(pocket, c, r, Blocks.BARRIER.defaultBlockState());
        if (oldRadius < 0) {
            // The way back: a Quantum Gate, a few steps behind where you arrive.
            pocket.setBlock(new BlockPos(c.getX(), FLOOR_Y + 1, c.getZ() - 4), ModBlocks.QUANTUM_GATE.get().defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private static void cage(ServerLevel level, BlockPos c, int r, BlockState state) {
        int e = r + 1;
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        for (int y = FLOOR_Y + 1; y <= FLOOR_Y + WALL_HEIGHT; y++) {
            for (int i = -e; i <= e; i++) {
                level.setBlock(m.set(c.getX() + i, y, c.getZ() - e), state, Block.UPDATE_CLIENTS);
                level.setBlock(m.set(c.getX() + i, y, c.getZ() + e), state, Block.UPDATE_CLIENTS);
                level.setBlock(m.set(c.getX() - e, y, c.getZ() + i), state, Block.UPDATE_CLIENTS);
                level.setBlock(m.set(c.getX() + e, y, c.getZ() + i), state, Block.UPDATE_CLIENTS);
            }
        }
        for (int dx = -e; dx <= e; dx++) {
            for (int dz = -e; dz <= e; dz++) {
                level.setBlock(m.set(c.getX() + dx, FLOOR_Y + WALL_HEIGHT + 1, c.getZ() + dz), state, Block.UPDATE_CLIENTS);
            }
        }
    }

    /** Brings a player who fell off the edge back to the middle of the space they are in. */
    public static void rescue(ServerPlayer player) {
        int index = Math.round((float) player.getX() / SPACING);
        player.teleportTo(player.serverLevel(), index * SPACING + 0.5, FLOOR_Y + 1.0, 0.5, player.getYRot(), 0.0F);
        player.fallDistance = 0.0F;
    }
}
