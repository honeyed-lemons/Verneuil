package com.honeyedlemons.verneuli.client.layers;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.client.model.AbstractGemModel;
import com.honeyedlemons.verneuli.client.renderer.entity.renderstates.GemRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class GemLayer extends RenderLayer<GemRenderState, AbstractGemModel>{

    public GemLayer(RenderLayerParent<GemRenderState, AbstractGemModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, @NotNull GemRenderState renderState, float yRot, float xRot) {
        if (renderState.gemVariant.layers().isEmpty())
            return;

        if (renderState.gemAppearanceData == null)
        {
            return;
        }

        var layers = renderState.gemVariant.layers().get();

        layers.forEach((layerData)->
        {
            String name = layerData.layerName();
            String palette = layerData.paletteName().isPresent() ? layerData.paletteName().get() : null;
            Boolean isVariant = layerData.isVariant();
            String variantName = isVariant ? renderState.gemAppearanceData.getLayerData().get(name) : null;

            ResourceLocation resourceLocation = variantName != null
                    ? getVariantLocation(renderState,name,variantName) : getResourceLocation(renderState,name);
            RenderType rendertype = RenderType.entityTranslucent(resourceLocation);
            int index = layers.indexOf(layerData);
            nodeCollector.order(index).submitModel(
                    this.getParentModel(),
                    renderState,
                    poseStack,
                    rendertype,
                    packedLight,
                    LivingEntityRenderer.getOverlayCoords(renderState, 0.0F),
                    renderState.gemAppearanceData.getColorData().getOrDefault(palette, Color.WHITE.getRGB()),
                    null,
                    renderState.outlineColor,
                    null);
        });
    }

    public ResourceLocation getResourceLocation(GemRenderState renderState, String name) {
        return ResourceLocation.fromNamespaceAndPath(Verneuil.MODID,
                "textures/entity/gems/" + renderState.entityType.toShortString() + "/" + name + ".png");
    }

    public ResourceLocation getVariantLocation(GemRenderState renderState, String name, String variant) {
        return ResourceLocation.fromNamespaceAndPath(Verneuil.MODID,
                "textures/entity/gems/" + renderState.entityType.toShortString() + "/" + name + "/" + variant + ".png");
    }
}
