package dev.quantumassembly;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The rotation comes in here. The port is a normal Create consumer: while the multiblock is assembled and you own the
 * Power upgrade, it puts a stress load on the network that matches the stress capacity your Pocket Power Output offers
 * (so nothing is free). Without the upgrade, or while disassembled, it puts no load on the network at all.
 */
public class KineticPortBlockEntity extends KineticBlockEntity {
    /** Stress per RPM we put on the network right now (also what the client shows on the goggles). */
    private float impact;
    private int checkTimer;

    public KineticPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KINETIC_PORT.get(), pos, state);
    }

    /** The load we should have right now, from the Power level of the owner of the multiblock we are part of. */
    private float wantedImpact() {
        if (level == null || level.isClientSide || level.getServer() == null || !getBlockState().getValue(Multiblock.ASSEMBLED)) {
            return 0.0F;
        }
        PocketControlsBlockEntity controls = PocketPower.controlsNear(level, worldPosition);
        if (controls == null || controls.getOwner() == null) {
            return 0.0F;
        }
        PocketData.Plot plot = PocketData.get(level.getServer()).existing(controls.getOwner());
        return plot == null ? 0.0F : PocketManager.POWER_CAPACITY[Math.min(plot.powerLevel, PocketManager.POWER_CAPACITY.length - 1)];
    }

    @Override
    public float calculateStressApplied() {
        if (level != null && !level.isClientSide) {
            impact = wantedImpact();
        }
        this.lastStressApplied = impact;
        return impact;
    }

    /** True while the port is really sending power into the pocket. */
    public boolean isActive() {
        return impact > 0.0F && getBlockState().getValue(Multiblock.ASSEMBLED);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) {
            return;
        }
        if (--checkTimer > 0) {
            return;
        }
        checkTimer = 20;
        float wanted = wantedImpact();
        if (wanted != impact) {
            impact = wanted;
            if (hasNetwork()) {
                KineticNetwork network = getOrCreateNetwork();
                network.updateStressFor(this, calculateStressApplied());
                network.updateStress();
            }
            sendData();
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putFloat("PortImpact", impact);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        impact = compound.getFloat("PortImpact");
        super.read(compound, registries, clientPacket);
    }
}
