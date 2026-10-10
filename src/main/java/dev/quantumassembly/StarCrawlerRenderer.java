package dev.quantumassembly;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/** Renders the Star Crawler with its purple shell and a glowing layer that stays bright in the dark. */
public class StarCrawlerRenderer extends MobRenderer<StarCrawler, StarCrawlerModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(QuantumAssembly.MODID, "textures/entity/star_crawler.png");
    private static final ResourceLocation GLOW =
            ResourceLocation.fromNamespaceAndPath(QuantumAssembly.MODID, "textures/entity/star_crawler_glow.png");

    public StarCrawlerRenderer(EntityRendererProvider.Context context) {
        super(context, new StarCrawlerModel(context.bakeLayer(StarCrawlerModel.LAYER)), 0.7F);
        this.addLayer(new GlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(StarCrawler entity) {
        return TEXTURE;
    }

    private static class GlowLayer extends EyesLayer<StarCrawler, StarCrawlerModel> {
        GlowLayer(RenderLayerParent<StarCrawler, StarCrawlerModel> parent) {
            super(parent);
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(GLOW);
        }
    }
}
