package dev.quantumassembly;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** The Star Crawler's own model: an armored body, a glowing abdomen, a head with mandibles and antennae, six legs. */
public class StarCrawlerModel extends HierarchicalModel<StarCrawler> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(QuantumAssembly.MODID, "star_crawler"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart abdomen;
    private final ModelPart antennaLeft;
    private final ModelPart antennaRight;
    private final ModelPart[] rightLegs = new ModelPart[3];
    private final ModelPart[] leftLegs = new ModelPart[3];

    public StarCrawlerModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.abdomen = root.getChild("abdomen");
        this.antennaLeft = this.head.getChild("antenna_left");
        this.antennaRight = this.head.getChild("antenna_right");
        for (int i = 0; i < 3; i++) {
            this.rightLegs[i] = root.getChild("leg_right_" + i);
            this.leftLegs[i] = root.getChild("leg_left_" + i);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-6.0F, -4.0F, -8.0F, 12.0F, 8.0F, 16.0F), PartPose.offset(0.0F, 15.0F, 0.0F));
        p.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 24)
                .addBox(-5.0F, -4.0F, 0.0F, 10.0F, 8.0F, 12.0F), PartPose.offset(0.0F, 16.0F, 7.0F));
        PartDefinition head = p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 44)
                .addBox(-4.0F, -3.0F, -7.0F, 8.0F, 6.0F, 7.0F), PartPose.offset(0.0F, 15.0F, -8.0F));
        head.addOrReplaceChild("mandible_left", CubeListBuilder.create().texOffs(32, 44)
                .addBox(0.0F, 0.0F, -3.0F, 1.0F, 3.0F, 3.0F), PartPose.offsetAndRotation(1.5F, 1.0F, -7.0F, 0.0F, 0.35F, 0.0F));
        head.addOrReplaceChild("mandible_right", CubeListBuilder.create().texOffs(32, 44)
                .addBox(-1.0F, 0.0F, -3.0F, 1.0F, 3.0F, 3.0F), PartPose.offsetAndRotation(-1.5F, 1.0F, -7.0F, 0.0F, -0.35F, 0.0F));
        head.addOrReplaceChild("antenna_left", CubeListBuilder.create().texOffs(40, 44)
                .addBox(-0.5F, -0.5F, -6.0F, 1.0F, 1.0F, 6.0F), PartPose.offsetAndRotation(2.0F, -2.5F, -6.0F, -0.5F, 0.35F, 0.0F));
        head.addOrReplaceChild("antenna_right", CubeListBuilder.create().texOffs(40, 44)
                .addBox(-0.5F, -0.5F, -6.0F, 1.0F, 1.0F, 6.0F), PartPose.offsetAndRotation(-2.0F, -2.5F, -6.0F, -0.5F, -0.35F, 0.0F));
        float[] z = {-4.0F, 0.0F, 4.0F};
        for (int i = 0; i < 3; i++) {
            p.addOrReplaceChild("leg_right_" + i, CubeListBuilder.create().texOffs(0, 57)
                    .addBox(-10.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F), PartPose.offset(-5.0F, 15.0F, z[i]));
            p.addOrReplaceChild("leg_left_" + i, CubeListBuilder.create().texOffs(0, 57)
                    .addBox(0.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F), PartPose.offset(5.0F, 15.0F, z[i]));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(StarCrawler entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
        this.head.xRot = headPitch * ((float) Math.PI / 180.0F);
        this.abdomen.xRot = Mth.sin(ageInTicks * 0.1F) * 0.04F;
        this.antennaLeft.xRot = -0.5F + Mth.sin(ageInTicks * 0.15F) * 0.12F;
        this.antennaRight.xRot = -0.5F + Mth.sin(ageInTicks * 0.15F + 1.5F) * 0.12F;

        float[] spread = {-0.6F, 0.0F, 0.6F};      // front, middle, rear
        float[] phase = {0.0F, (float) Math.PI, 0.0F};
        for (int i = 0; i < 3; i++) {
            float swing = -(Mth.cos(limbSwing * 0.6662F * 2.0F + phase[i]) * 0.4F) * limbSwingAmount;
            float lift = Math.abs(Mth.sin(limbSwing * 0.6662F + phase[i])) * 0.4F * limbSwingAmount;
            this.rightLegs[i].yRot = spread[i] + swing;
            this.rightLegs[i].zRot = -0.55F - lift;
            this.leftLegs[i].yRot = -spread[i] - swing;
            this.leftLegs[i].zRot = 0.55F + lift;
        }
    }
}
