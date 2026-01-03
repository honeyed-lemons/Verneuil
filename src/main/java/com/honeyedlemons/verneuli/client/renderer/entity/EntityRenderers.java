package com.honeyedlemons.verneuli.client.renderer.entity;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.client.model.VerneuilLayerDefinitions;
import com.honeyedlemons.verneuli.entities.VerneuilEntities;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid = Verneuil.MODID)
public class EntityRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VerneuilEntities.QUARTZ.get(), context -> new QuartzRenderer(context, VerneuilLayerDefinitions.QUARTZ_ARMOR));
        event.registerEntityRenderer(VerneuilEntities.GEM_ITEM_ENTITY.get(), ItemEntityRenderer::new);
    }
}
