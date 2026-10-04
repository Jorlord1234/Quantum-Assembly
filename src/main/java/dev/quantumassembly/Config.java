package dev.quantumassembly;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue CATALYST_CAPACITY_BUCKETS = BUILDER
            .comment("How many buckets of Liquid Experience the Experience Catalyst can hold.")
            .defineInRange("catalystCapacityBuckets", 999, 1, 9999);

    public static final ModConfigSpec.BooleanValue CATALYST_CONSUMED = BUILDER
            .comment("true: using the Experience Catalyst on a Blaze Burner uses up the whole item (like a Cake of Nebulae).",
                    "false: it keeps the item and only uses 1 bucket of Liquid Experience.")
            .define("catalystConsumedOnUse", true);

    public static final ModConfigSpec.ConfigValue<String> MASTER_CHEF_ADVANCEMENT = BUILDER
            .comment("Advancement id that gives the Master Chef's Cauldron and Chef's Hat (example: farmersdelight:main/master_chef).",
                    "Any advancement titled 'Master Chef' or with 'master_chef' in its id also works.")
            .define("masterChefAdvancement", "");

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean catalystConsumed() {
        try {
            return CATALYST_CONSUMED.get();
        } catch (IllegalStateException notLoadedYet) {
            return true;
        }
    }

    public static String masterChefAdvancement() {
        try {
            return MASTER_CHEF_ADVANCEMENT.get();
        } catch (IllegalStateException notLoadedYet) {
            return "";
        }
    }

    /** Capacity in millibuckets (1 bucket = 1000 mB). */
    public static int capacityMb() {
        try {
            return CATALYST_CAPACITY_BUCKETS.get() * 1000;
        } catch (IllegalStateException notLoadedYet) {
            return 999 * 1000;
        }
    }
}
