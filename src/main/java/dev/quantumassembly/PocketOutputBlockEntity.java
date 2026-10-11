package dev.quantumassembly;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A generator whose speed is the speed of the Kinetic Port of the multiblock that belongs to this pocket space, and
 * whose stress capacity comes from the owner's Power upgrade. Every few ticks it looks at the port (the chunk with the
 * multiblock has to be loaded) and updates its rotation when something changed.
 */
public class PocketOutputBlockEntity extends GeneratingKineticBlockEntity {
    /** RPM that arrives at the port (negative = the other way round). */
    private float inputSpeed;
    /** Stress capacity per RPM we offer. */
    private float capacityPerRpm;
    private int checkTimer;

    public PocketOutputBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getGeneratedSpeed() {
        return convertToDirection(inputSpeed, getBlockState().getValue(DirectionalKineticBlock.FACING));
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = inputSpeed == 0.0F ? 0.0F : capacityPerRpm;
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide || level.getServer() == null) {
            return;
        }
        if (--checkTimer > 0) {
            return;
        }
        checkTimer = 4;
        float speed = 0.0F;
        float capacity = 0.0F;
        MinecraftServer server = level.getServer();
        PocketData.Plot plot = PocketData.get(server).byIndex(PocketManager.plotIndexAt(worldPosition.getX()));
        if (plot != null && plot.powerLevel > 0 && !plot.controlsDim.isEmpty()) {
            ServerLevel home = server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(plot.controlsDim)));
            KineticPortBlockEntity best = home == null ? null : fastestPort(home, plot.controlsPos);
            if (best != null) {
                speed = best.getSpeed();
                capacity = PocketManager.POWER_CAPACITY[Math.min(plot.powerLevel, PocketManager.POWER_CAPACITY.length - 1)];
            }
        }
        if (speed != inputSpeed || capacity != capacityPerRpm) {
            inputSpeed = speed;
            capacityPerRpm = capacity;
            updateGeneratedRotation();
        }
    }

    private static KineticPortBlockEntity fastestPort(Level home, BlockPos controls) {
        KineticPortBlockEntity best = null;
        for (BlockPos p : BlockPos.betweenClosed(controls.offset(-2, -2, -2), controls.offset(2, 2, 2))) {
            if (!home.hasChunkAt(p)) {
                continue;
            }
            if (home.getBlockEntity(p) instanceof KineticPortBlockEntity port && port.isActive()
                    && (best == null || Math.abs(port.getSpeed()) > Math.abs(best.getSpeed()))) {
                best = port;
            }
        }
        return best;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putFloat("InputSpeed", inputSpeed);
        compound.putFloat("PocketCapacity", capacityPerRpm);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        inputSpeed = compound.getFloat("InputSpeed");
        capacityPerRpm = compound.getFloat("PocketCapacity");
        super.read(compound, registries, clientPacket);
    }
}
