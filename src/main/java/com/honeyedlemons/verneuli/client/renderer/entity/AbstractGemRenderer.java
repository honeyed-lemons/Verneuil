package com.honeyedlemons.verneuli.client.renderer.entity;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.client.model.AbstractGemModel;
import com.honeyedlemons.verneuli.client.renderer.entity.renderstates.GemRenderState;
import com.honeyedlemons.verneuli.shared.entities.gems.AbstractGem;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractGemRenderer<T extends AbstractGem, S extends GemRenderState> extends LivingEntityRenderer<AbstractGem, GemRenderState, AbstractGemModel> {
    public AbstractGemRenderer(EntityRendererProvider.Context context, AbstractGemModel model, float shadowRadius) {
        super(context, model, shadowRadius);

    }
    @Override
    public @NotNull GemRenderState createRenderState() {
        return new GemRenderState();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GemRenderState renderState) {
        var type = renderState.entityType.toShortString();
        return ResourceLocation.fromNamespaceAndPath(Verneuil.MODID,"/textures/entity/gems/" + type + "/" + type + ".png");
    }

    @Override
    public void extractRenderState(@NotNull AbstractGem type, @NotNull GemRenderState renderState, float partialTick) {
        super.extractRenderState(type, renderState, partialTick);
        renderState.colors = type.getGemColors();
        renderState.layerVariants = type.getGemLayerData();
        renderState.gemVariant = type.getGemVariant();
    }
}
