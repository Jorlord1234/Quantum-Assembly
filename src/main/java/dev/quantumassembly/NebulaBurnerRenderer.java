package dev.quantumassembly;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Draws the Nebula Blaze Burner's head and rods the way Create draws its own burner: the head bobs, it turns to look
 * at the nearest player, and when lit the two sets of rods float up and down.
 */
public class NebulaBurnerRenderer implements BlockEntityRenderer<NebulaBurnerBlockEntity> {
    public static final ModelResourceLocation HEAD_COLD = part("part_head_cold");
    public static final ModelResourceLocation HEAD_LIT = part("part_head_lit");
    public static final ModelResourceLocation RODS_SMALL = part("part_rods_small");
    public static final ModelResourceLocation RODS_LARGE = part("part_rods_large");

    private static ModelResourceLocation part(String name) {
        return ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath(QuantumAssembly.MODID, "block/nebula_burner/" + name));
    }

    public NebulaBurnerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(NebulaBurnerBlockEntity burner, float partialTick, PoseStack poseStack, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        Level level = burner.getLevel();
        if (level == null) {
            return;
        }
        BlockState state = burner.getBlockState();
        boolean lit = NebulaBurnerBlock.isLit(state);
        float time = level.getGameTime() + partialTick;
        float renderTick = time + (burner.getBlockPos().hashCode() % 13) * 16.0F;
        float mult = lit ? 64.0F : 16.0F;
        float phase = renderTick / 16.0F;
        float offset = Mth.sin(phase) / mult;
        float offset1 = Mth.sin(phase + (float) Math.PI) / mult;
        float offset2 = Mth.sin(phase + (float) Math.PI / 2.0F) / mult;
        int light = lit ? LightTexture.FULL_BRIGHT : packedLight;

        // turn the head toward the nearest player (same formula Create uses), otherwise face the block's direction
        float target = 180.0F - state.getValue(NebulaBurnerBlock.FACING).toYRot();
        BlockPos_ pos = new BlockPos_(burner);
        Player player = level.getNearestPlayer(pos.x, pos.y, pos.z, 8.0, false);
        if (player != null) {
            double dx = player.getX() - pos.x;
            double dz = player.getZ() - pos.z;
            target = (float) Math.toDegrees(-Mth.atan2(dz, dx)) - 90.0F;
        }
        if (!burner.headAngleReady) {
            burner.headAngle = target;
            burner.headAngleReady = true;
        }
        float diff = Mth.wrapDegrees(target - burner.headAngle);
        burner.headAngle += diff * 0.12F;

        ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());

        poseStack.pushPose();
        poseStack.pushPose();
        poseStack.translate(0.0F, offset, 0.0F);
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(burner.headAngle));
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        draw(renderer, poseStack, consumer, state, lit ? HEAD_LIT : HEAD_COLD, light, packedOverlay);
        poseStack.popPose();

        if (lit) {
            poseStack.pushPose();
            poseStack.translate(0.0F, offset1 + 0.125F, 0.0F);
            draw(renderer, poseStack, consumer, state, RODS_SMALL, light, packedOverlay);
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.translate(0.0F, offset2 - 3.0F / 16.0F, 0.0F);
            draw(renderer, poseStack, consumer, state, RODS_LARGE, light, packedOverlay);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private static void draw(ModelBlockRenderer renderer, PoseStack poseStack, VertexConsumer consumer, BlockState state,
                             ModelResourceLocation location, int light, int overlay) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(location);
        renderer.renderModel(poseStack.last(), consumer, state, model, 1.0F, 1.0F, 1.0F, light, overlay);
    }

    /** The middle of the block, as doubles. */
    private static final class BlockPos_ {
        final double x;
        final double y;
        final double z;

        BlockPos_(NebulaBurnerBlockEntity burner) {
            this.x = burner.getBlockPos().getX() + 0.5;
            this.y = burner.getBlockPos().getY() + 0.5;
            this.z = burner.getBlockPos().getZ() + 0.5;
        }
    }
}
