package com.honeyedlemons.verneuli.client.renderer.entity;

import com.honeyedlemons.verneuli.client.layers.GemLayer;
import com.honeyedlemons.verneuli.client.model.VerneuilLayerDefinitions;
import com.honeyedlemons.verneuli.client.model.QuartzModel;
import com.honeyedlemons.verneuli.client.renderer.entity.renderstates.GemRenderState;
import com.honeyedlemons.verneuli.entities.gems.Quartz;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class QuartzRenderer extends AbstractGemRenderer<Quartz, GemRenderState> {
    public QuartzRenderer(EntityRendererProvider.Context context,ArmorModelSet<ModelLayerLocation> armorModelSet) {
        super(context, new QuartzModel(context.bakeLayer(VerneuilLayerDefinitions.BASE_GEM_LAYER)), 0.5f);
        this.addLayer(new GemLayer(this));
        this.addLayer(new HumanoidArmorLayer<>(
                        this,
                        ArmorModelSet.bake(armorModelSet, context.getModelSet(), QuartzModel::new),
                        context.getEquipmentRenderer())
        );
    }

}