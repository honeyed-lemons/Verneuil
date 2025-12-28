package com.honeyedlemons.verneuli.client.renderer.entity;

import com.honeyedlemons.verneuli.client.layers.GemLayer;
import com.honeyedlemons.verneuli.client.model.VerneuilLayerDefinitions;
import com.honeyedlemons.verneuli.client.model.QuartzModel;
import com.honeyedlemons.verneuli.client.renderer.entity.renderstates.GemRenderState;
import com.honeyedlemons.verneuli.shared.entities.gems.Quartz;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class QuartzRenderer extends AbstractGemRenderer<Quartz, GemRenderState> {
    public QuartzRenderer(EntityRendererProvider.Context context) {
        super(context, new QuartzModel(context.bakeLayer(VerneuilLayerDefinitions.BASE_GEM_LAYER)), 0.5f);
        this.addLayer(new GemLayer(this));
    }
}