package dev.quantumassembly;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.serialization.MapCodec;

/** A non-full block whose front faces the player when placed. The shape is written for NORTH. */
public class FacingShapedBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<FacingShapedBlock> CODEC = simpleCodec(p -> new FacingShapedBlock(p, Shapes.block()));
    private final VoxelShape[] shapes = new VoxelShape[4];

    public FacingShapedBlock(Properties properties, VoxelShape northShape) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
        for (Direction d : Direction.Plane.HORIZONTAL) {
            shapes[d.get2DDataValue()] = rotate(northShape, d);
        }
    }

    private static VoxelShape rotate(VoxelShape shape, Direction to) {
        int turns = (to.get2DDataValue() + 2) % 4; // NORTH(2) -> 0 turns
        VoxelShape[] result = {shape};
        for (int i = 0; i < turns; i++) {
            VoxelShape[] next = {Shapes.empty()};
            result[0].forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                    next[0] = Shapes.or(next[0], Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2)));
            result[0] = next[0];
        }
        return result[0];
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
