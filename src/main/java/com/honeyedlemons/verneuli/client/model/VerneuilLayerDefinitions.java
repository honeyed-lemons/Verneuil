package com.honeyedlemons.verneuli.client.model;

import com.honeyedlemons.verneuli.Verneuil;
import net.minecraft.client.model.geom.ModelLayerLocation;
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

    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Add our layer here.
        event.registerLayerDefinition(BASE_GEM_LAYER, QuartzModel::createBodyLayer);
    }
}
