package dev.quantumassembly;

import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.SilverfishRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Silverfish;

/** The Silverfish model with our purple skin and a glowing layer on top (it stays bright in the dark). */
public class StarMiteRenderer extends SilverfishRenderer {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(QuantumAssembly.MODID, "textures/entity/star_mite.png");
    private static final ResourceLocation GLOW =
            ResourceLocation.fromNamespaceAndPath(QuantumAssembly.MODID, "textures/entity/star_mite_glow.png");

    public StarMiteRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new GlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Silverfish entity) {
        return TEXTURE;
    }

    private static class GlowLayer extends EyesLayer<Silverfish, SilverfishModel<Silverfish>> {
        GlowLayer(RenderLayerParent<Silverfish, SilverfishModel<Silverfish>> parent) {
            super(parent);
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(GLOW);
        }
    }
}
