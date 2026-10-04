package dev.quantumassembly;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue CATALYST_CAPACITY_BUCKETS = BUILDER
            .comment("How many buckets of Liquid Experience the Experience Catalyst can hold.")
            .defineInRange("catalystCapacityBuckets", 999, 1, 9999);

    static final ModConfigSpec SPEC = BUILDER.build();

    /** Capacity in millibuckets (1 bucket = 1000 mB). */
    public static int capacityMb() {
        try {
            return CATALYST_CAPACITY_BUCKETS.get() * 1000;
        } catch (IllegalStateException notLoadedYet) {
            return 999 * 1000;
        }
    }
}
