package dev.quantumassembly;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

/**
 * The Quantum Engineer's trades. Deliberately stingy: he buys lab leftovers for emeralds and sells a few
 * decor and utility items at high prices. Nothing from the main progression is sold (Nova Template,
 * Experience Catalyst, Kit, Pocket parts, Cake of Nebulae) and Starglass can only be sold to him.
 */
@EventBusSubscriber(modid = QuantumAssembly.MODID)
public class VillagerEvents {
    @SubscribeEvent
    public static void onTrades(VillagerTradesEvent event) {
        if (event.getType() != ModVillagers.QUANTUM_ENGINEER.get()) {
            return;
        }
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        // Novice
        add(trades, 1, new BasicItemListing(new ItemStack(Items.AMETHYST_SHARD, 24), new ItemStack(Items.EMERALD), 12, 2, 0.05F));
        add(trades, 1, new BasicItemListing(new ItemStack(Items.OBSIDIAN, 10), new ItemStack(Items.EMERALD), 12, 2, 0.05F));
        add(trades, 1, new BasicItemListing(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.END_ROD, 4), 8, 2, 0.05F));
        // Apprentice
        add(trades, 2, new BasicItemListing(new ItemStack(ModItems.STARGLASS.get(), 4), new ItemStack(Items.EMERALD), 8, 5, 0.05F));
        add(trades, 2, new BasicItemListing(new ItemStack(Items.GLOW_INK_SAC, 4), new ItemStack(Items.EMERALD), 12, 5, 0.05F));
        add(trades, 2, new BasicItemListing(new ItemStack(Items.EMERALD, 6), new ItemStack(Items.ENDER_PEARL), 6, 5, 0.05F));
        // Journeyman
        add(trades, 3, new BasicItemListing(new ItemStack(Items.CRYING_OBSIDIAN, 6), new ItemStack(Items.EMERALD, 2), 8, 10, 0.05F));
        add(trades, 3, new BasicItemListing(new ItemStack(Items.EMERALD, 7), new ItemStack(Items.EXPERIENCE_BOTTLE, 2), 6, 10, 0.05F));
        add(trades, 3, new BasicItemListing(new ItemStack(Items.EMERALD, 8), new ItemStack(Items.TINTED_GLASS, 4), 6, 10, 0.05F));
        // Expert
        add(trades, 4, new BasicItemListing(new ItemStack(Items.EMERALD, 40), new ItemStack(Items.ENDER_CHEST), 2, 15, 0.05F));
        add(trades, 4, new BasicItemListing(new ItemStack(Items.EMERALD, 48), new ItemStack(Items.ECHO_SHARD), 1, 15, 0.05F));
        // Master
        add(trades, 5, new BasicItemListing(new ItemStack(Items.EMERALD, 12), new ItemStack(Items.AMETHYST_BLOCK, 16), 4, 30, 0.05F));
        add(trades, 5, new BasicItemListing(new ItemStack(Items.EMERALD, 64), new ItemStack(ModItems.STARGLASS.get(), 4),
                new ItemStack(Items.TOTEM_OF_UNDYING), 1, 30, 0.05F));
    }

    private static void add(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, int level, VillagerTrades.ItemListing listing) {
        trades.computeIfAbsent(level, k -> new ArrayList<>()).add(listing);
    }
}
