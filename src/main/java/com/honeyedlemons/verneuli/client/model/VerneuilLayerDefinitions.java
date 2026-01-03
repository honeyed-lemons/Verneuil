package com.honeyedlemons.verneuli.client.model;

import com.honeyedlemons.verneuli.Verneuil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Verneuil.MODID)
public class VerneuilLayerDefinitions {
    // Our ModelLayerLocation.
    public static final ModelLayerLocation BASE_GEM_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Verneuil.MODID, "gem"),
            "main"
    );
	public static final CubeDeformation OUTER_ARMOR_DEFORMATION = new CubeDeformation(1.05F);
	public static final CubeDeformation INNER_ARMOR_DEFORMATION = new CubeDeformation(1.2F);
    public static final ArmorModelSet<ModelLayerLocation> QUARTZ_ARMOR = registerArmorSet("armor");
    public static final ArmorModelSet<LayerDefinition> QUARTZ_ARMOR_LAYER = QuartzModel.createArmorMeshSet(INNER_ARMOR_DEFORMATION,OUTER_ARMOR_DEFORMATION)
            .map(p_432244_ -> LayerDefinition.create(p_432244_, 64, 32));
    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Add our layer here.
        event.registerLayerDefinition(BASE_GEM_LAYER, QuartzModel::createBodyLayer);
        event.registerLayerDefinition(QUARTZ_ARMOR.head(), QUARTZ_ARMOR_LAYER::head);
        event.registerLayerDefinition(QUARTZ_ARMOR.chest(), QUARTZ_ARMOR_LAYER::chest);
        event.registerLayerDefinition(QUARTZ_ARMOR.legs(), QUARTZ_ARMOR_LAYER::legs);
        event.registerLayerDefinition(QUARTZ_ARMOR.feet(), QUARTZ_ARMOR_LAYER::feet);
    }

    public static ArmorModelSet<ModelLayerLocation> registerArmorSet(String path) {
        var resourceLocation = ResourceLocation.fromNamespaceAndPath(Verneuil.MODID,path);
		return new ArmorModelSet<>(
				new ModelLayerLocation(resourceLocation, "helmet"),
				new ModelLayerLocation(resourceLocation, "chestplate"),
				new ModelLayerLocation(resourceLocation, "leggings"),
				new ModelLayerLocation(resourceLocation, "boots"));
    }

}
