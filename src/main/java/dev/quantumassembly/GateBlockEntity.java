package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Remembers which Pocket Controls this Gate is linked to. */
public class GateBlockEntity extends BlockEntity {
    private String linkDim = "";
    private BlockPos linkPos = BlockPos.ZERO;

    public GateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GATE.get(), pos, state);
    }

    public boolean isLinked() {
        return !linkDim.isEmpty();
    }

    public String getLinkDim() {
        return linkDim;
    }

    public BlockPos getLinkPos() {
        return linkPos;
    }

    public void link(String dim, BlockPos pos) {
        this.linkDim = dim;
        this.linkPos = pos;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("dim", linkDim);
        tag.putLong("pos", linkPos.asLong());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        linkDim = tag.getString("dim");
        linkPos = BlockPos.of(tag.getLong("pos"));
    }
}
