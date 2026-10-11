package dev.quantumassembly;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

/** Draws the lid of the Quantum Assembly Kit and swings it open around its back edge, like a Create toolbox. */
public class KitRenderer implements BlockEntityRenderer<KitBlockEntity> {
    public static final ModelResourceLocation LID = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(QuantumAssembly.MODID, "block/quantum_assembly_kit_lid"));

    /** The hinge, in block coordinates (the back edge of the top of the body). */
    private static final float HINGE_Y = 6.0F / 16.0F;
    private static final float HINGE_Z = 12.0F / 16.0F;

    public KitRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(KitBlockEntity kit, float partialTick, PoseStack poseStack, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        BlockState state = kit.getBlockState();
        if (!state.is(ModBlocks.QUANTUM_ASSEMBLY_KIT.get())) {
            return;
        }
        float open = Mth.lerp(partialTick, kit.lastLid, kit.lid);
        open = 1.0F - (1.0F - open) * (1.0F - open);
        float angle = open * 105.0F;
        // the rotation the blockstate applies to this facing (north = 0, east = 90, south = 180, west = 270)
        float yRot = (state.getValue(KitBlock.FACING).toYRot() + 180.0F) % 360.0F;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        poseStack.translate(0.0F, HINGE_Y, HINGE_Z);
        poseStack.mulPose(Axis.XP.rotationDegrees(angle));
        poseStack.translate(0.0F, -HINGE_Y, -HINGE_Z);
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(LID);
        ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        renderer.renderModel(poseStack.last(), buffers.getBuffer(RenderType.cutout()), state, model, 1.0F, 1.0F, 1.0F,
                packedLight, packedOverlay);
        poseStack.popPose();
    }
}
