package dev.quantumassembly;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(QuantumAssembly.MODID);

    public static final DeferredItem<ExperienceCatalystItem> EXPERIENCE_CATALYST = ITEMS.registerItem(
            "experience_catalyst", ExperienceCatalystItem::new, new Item.Properties().rarity(Rarity.EPIC));

    public static final DeferredItem<IncompleteCatalystItem> INCOMPLETE_EXPERIENCE_CATALYST = ITEMS.registerItem(
            "incomplete_experience_catalyst", IncompleteCatalystItem::new, new Item.Properties());

    public static final DeferredItem<Item> NOVA_TEMPLATE = ITEMS.registerSimpleItem("nova_template");
    public static final DeferredItem<UncompletedNovaCoinItem> UNCOMPLETED_NOVA_COIN = ITEMS.registerItem(
            "uncompleted_nova_coin", UncompletedNovaCoinItem::new, new Item.Properties());
    public static final DeferredItem<Item> NOVA_COIN = ITEMS.registerSimpleItem("nova_coin",
            new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<Item> CAKE_OF_NEBULAE = ITEMS.registerSimpleItem("cake_of_nebulae",
            new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredItem<Item> STARGLASS = ITEMS.registerSimpleItem("starglass");

    public static final DeferredItem<ChefsHatItem> CHEFS_HAT = ITEMS.registerItem(
            "chefs_hat", ChefsHatItem::new, new Item.Properties());

    public static final DeferredItem<BlockItem> STARGLASS_ORE = ITEMS.registerSimpleBlockItem(
            "starglass_ore", ModBlocks.STARGLASS_ORE);
    public static final DeferredItem<BlockItem> QUANTUM_ASSEMBLY_KIT = ITEMS.registerSimpleBlockItem(
            "quantum_assembly_kit", ModBlocks.QUANTUM_ASSEMBLY_KIT);
    public static final DeferredItem<BlockItem> MASTER_CHEFS_CAULDRON = ITEMS.registerSimpleBlockItem(
            "master_chefs_cauldron", ModBlocks.MASTER_CHEFS_CAULDRON);
}
