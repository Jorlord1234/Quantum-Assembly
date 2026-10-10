package dev.quantumassembly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = QuantumAssembly.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    public static final ModelLayerLocation CHEF_HAT = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(QuantumAssembly.MODID, "chefs_hat"), "main");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // the burner's cage has see-through gaps: draw it with the cutout render type
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(ModBlocks.NEBULA_BLAZE_BURNER.get(), RenderType.cutout()));
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        // the animated head and rods of the Nebula Blaze Burner are drawn by its block entity renderer
        event.register(NebulaBurnerRenderer.HEAD_COLD);
        event.register(NebulaBurnerRenderer.HEAD_LIT);
        event.register(NebulaBurnerRenderer.RODS_SMALL);
        event.register(NebulaBurnerRenderer.RODS_LARGE);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CHEF_HAT, ChefHatModel::createLayer);
        event.registerLayerDefinition(StarCrawlerModel.LAYER, StarCrawlerModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.STAR_CRAWLER.get(), StarCrawlerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.NEBULA_BURNER.get(), NebulaBurnerRenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.KIT.get(), KitScreen::new);
        event.register(ModMenus.CONTROLS.get(), PocketControlsScreen::new);
    }

    @SubscribeEvent
    public static void registerExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                return new ChefHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(CHEF_HAT));
            }
        }, ModItems.CHEFS_HAT.get());
    }
}
