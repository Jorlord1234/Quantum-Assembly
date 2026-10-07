package dev.quantumassembly;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

/** A ridiculously tall chef hat: a long tube with a big puff on top. */
public class ChefHatModel extends HumanoidModel<LivingEntity> {
    public ChefHatModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();
        // Empty head and hat parts: we only want the hat cubes, not the normal head.
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        // The top of the head is at y = -8. The tube is 16 tall, the puff on top is 6 tall and a little wider.
        head.addOrReplaceChild("hat_tube",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -24.0F, -4.0F, 8.0F, 16.0F, 8.0F),
                PartPose.ZERO);
        head.addOrReplaceChild("hat_puff",
                CubeListBuilder.create().texOffs(0, 26).addBox(-5.0F, -30.0F, -5.0F, 10.0F, 6.0F, 10.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }
}
