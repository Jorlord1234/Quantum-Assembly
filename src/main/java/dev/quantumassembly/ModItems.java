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
    public static final DeferredItem<NovaCoinItem> NOVA_COIN = ITEMS.registerItem("nova_coin",
            NovaCoinItem::new, new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<Item> CAKE_OF_NEBULAE = ITEMS.registerSimpleItem("cake_of_nebulae",
            new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredItem<Item> CAKE_BASE = ITEMS.registerSimpleItem("cake_base");
    public static final DeferredItem<Item> NEBULA_BATTER = ITEMS.registerSimpleItem("nebula_batter");
    /** Hidden helper item. Create: Enchantment Industry sees it as a super experience fuel. Never obtainable. */
    public static final DeferredItem<Item> SUPER_EXPERIENCE_CHARGE = ITEMS.registerSimpleItem("super_experience_charge");
    public static final DeferredItem<Item> STARGLASS = ITEMS.registerSimpleItem("starglass");

    public static final DeferredItem<ChefsHatItem> CHEFS_HAT = ITEMS.registerItem(
            "chefs_hat", ChefsHatItem::new, new Item.Properties());

    public static final DeferredItem<BlockItem> STARGLASS_ORE = ITEMS.registerSimpleBlockItem(
            "starglass_ore", ModBlocks.STARGLASS_ORE);
    public static final DeferredItem<BlockItem> NEBULA_BLAZE_BURNER = ITEMS.registerSimpleBlockItem(
            "nebula_blaze_burner", ModBlocks.NEBULA_BLAZE_BURNER);
    /** Placeholder result of the ritual until the real Quantum Gate is designed. */
    public static final DeferredItem<Item> QUANTUM_GATE = ITEMS.registerSimpleItem("quantum_gate",
            new Item.Properties().rarity(Rarity.EPIC).stacksTo(1));
    public static final DeferredItem<BlockItem> QUANTUM_ASSEMBLY_KIT = ITEMS.registerSimpleBlockItem(
            "quantum_assembly_kit", ModBlocks.QUANTUM_ASSEMBLY_KIT);
    public static final DeferredItem<BlockItem> MASTER_CHEFS_CAULDRON = ITEMS.register(
            "master_chefs_cauldron", () -> new ReturningBlockItem(ModBlocks.MASTER_CHEFS_CAULDRON.get(), new Item.Properties()));
}
