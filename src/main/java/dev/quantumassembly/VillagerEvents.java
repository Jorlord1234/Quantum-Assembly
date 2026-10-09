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

/** The Quantum Engineer's trades. Nothing from the main progression (Nova Template, Catalyst, Kit) is sold. */
@EventBusSubscriber(modid = QuantumAssembly.MODID)
public class VillagerEvents {
    @SubscribeEvent
    public static void onTrades(VillagerTradesEvent event) {
        if (event.getType() != ModVillagers.QUANTUM_ENGINEER.get()) {
            return;
        }
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        // Novice
        add(trades, 1, new BasicItemListing(new ItemStack(Items.AMETHYST_SHARD, 16), new ItemStack(Items.EMERALD), 16, 2, 0.05F));
        add(trades, 1, new BasicItemListing(new ItemStack(Items.OBSIDIAN, 8), new ItemStack(Items.EMERALD), 12, 2, 0.05F));
        add(trades, 1, new BasicItemListing(new ItemStack(Items.EMERALD, 3), new ItemStack(Items.END_ROD, 8), 12, 2, 0.05F));
        // Apprentice
        add(trades, 2, new BasicItemListing(new ItemStack(ModItems.STARGLASS.get(), 2), new ItemStack(Items.EMERALD), 16, 5, 0.05F));
        add(trades, 2, new BasicItemListing(new ItemStack(Items.EMERALD, 6), new ItemStack(Items.ENDER_PEARL, 3), 8, 5, 0.05F));
        // Journeyman
        add(trades, 3, new BasicItemListing(new ItemStack(Items.EMERALD, 24), new ItemStack(Items.ECHO_SHARD), 4, 10, 0.05F));
        add(trades, 3, new BasicItemListing(new ItemStack(Items.EMERALD, 20), new ItemStack(Items.ENDER_CHEST), 4, 10, 0.05F));
        add(trades, 3, new BasicItemListing(new ItemStack(Items.CRYING_OBSIDIAN, 6), new ItemStack(Items.EMERALD, 2), 12, 10, 0.05F));
        // Expert
        add(trades, 4, new BasicItemListing(new ItemStack(Items.EMERALD, 40), new ItemStack(ModItems.POCKET_CASING.get()), 3, 15, 0.05F));
        add(trades, 4, new BasicItemListing(new ItemStack(Items.EMERALD, 48), new ItemStack(Items.TOTEM_OF_UNDYING), 2, 15, 0.05F));
        // Master
        add(trades, 5, new BasicItemListing(new ItemStack(Items.EMERALD, 40), new ItemStack(ModItems.STARGLASS.get(), 3),
                new ItemStack(ModItems.CAKE_OF_NEBULAE.get()), 2, 30, 0.05F));
        add(trades, 5, new BasicItemListing(new ItemStack(Items.EMERALD, 64), new ItemStack(Items.DIAMOND),
                new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), 1, 30, 0.05F));
    }

    private static void add(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, int level, VillagerTrades.ItemListing listing) {
        trades.computeIfAbsent(level, k -> new ArrayList<>()).add(listing);
    }
}
