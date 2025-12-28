package com.honeyedlemons.verneuli.client.renderer.entity;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.shared.entities.VerneuilEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid = Verneuil.MODID)
public class EntityRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VerneuilEntities.QUARTZ.get(), QuartzRenderer::new);
    }
}
