package dev.quantumassembly;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue CATALYST_CAPACITY_BUCKETS = BUILDER
            .comment("How many buckets of Liquid Experience the Experience Catalyst can hold.")
            .defineInRange("catalystCapacityBuckets", 999, 1, 9999);

    public static final ModConfigSpec.ConfigValue<String> MASTER_CHEF_ADVANCEMENT = BUILDER
            .comment("Advancement id that gives the Master Chef's Cauldron and Chef's Hat (example: farmersdelight:main/master_chef).",
                    "Any advancement titled 'Master Chef' or with 'master_chef' in its id also works.")
            .define("masterChefAdvancement", "");

    public static final ModConfigSpec.IntValue BURNER_LIT_SECONDS = BUILDER
            .comment("How long the Nebula Blaze Burner stays lit after a Cake of Nebulae (seconds). 0 = forever.")
            .defineInRange("nebulaBurnerLitSeconds", 300, 0, 86400);

    public static final ModConfigSpec.IntValue RITUAL_SECONDS = BUILDER
            .comment("How long the Quantum Assembly Kit ritual takes (seconds).")
            .defineInRange("ritualSeconds", 10, 1, 600);

    public static final ModConfigSpec.IntValue KIT_STACK_LIMIT = BUILDER
            .comment("How many items fit in one slot of the Quantum Assembly Kit (normal Minecraft is 64).")
            .defineInRange("kitStackLimit", 256, 64, 999);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static String masterChefAdvancement() {
        try {
            return MASTER_CHEF_ADVANCEMENT.get();
        } catch (IllegalStateException notLoadedYet) {
            return "";
        }
    }

    public static int burnerLitTicks() {
        try {
            return BURNER_LIT_SECONDS.get() * 20;
        } catch (IllegalStateException notLoadedYet) {
            return 300 * 20;
        }
    }

    public static int ritualTicks() {
        try {
            return RITUAL_SECONDS.get() * 20;
        } catch (IllegalStateException notLoadedYet) {
            return 10 * 20;
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

    public static int kitStackLimit() {
        try {
            return KIT_STACK_LIMIT.get();
        } catch (IllegalStateException notLoadedYet) {
            return 256;
        }
    }
}
