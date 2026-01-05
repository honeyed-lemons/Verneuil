package com.honeyedlemons.verneuli.client.renderer.entity;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.client.model.AbstractGemModel;
import com.honeyedlemons.verneuli.client.renderer.entity.renderstates.GemRenderState;
import com.honeyedlemons.verneuli.entities.gems.AbstractGem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class AbstractGemRenderer<T extends AbstractGem, S extends GemRenderState> extends HumanoidMobRenderer<AbstractGem, GemRenderState, AbstractGemModel> {
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
        return ResourceLocation.fromNamespaceAndPath(Verneuil.MODID, "/textures/entity/gems/" + type + "/" + type + ".png");
    }

    @Override
    public void extractRenderState(@NotNull AbstractGem type, @NotNull GemRenderState renderState, float partialTick) {
        super.extractRenderState(type, renderState, partialTick);
        renderState.gemAppearanceData = type.getGemAppearanceData();
        renderState.gemVariant = type.getGemVariant();
		if (renderState.deathTime > 0)
		{
			renderState.isInvisible = true;
		}
    }

    @Override
    public void submit(GemRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.gemVariant.variants().isEmpty())
            return;

		for (Map.Entry<String, List<String>> entry : renderState.gemVariant.variants().get().entrySet()) {
			String string = entry.getKey();
			if (!renderState.gemAppearanceData.getLayerData().containsKey(string))
				return;
		}

		super.submit(renderState,poseStack,nodeCollector,cameraRenderState);
    }
	@Override
	protected void setupRotations(@NotNull GemRenderState renderState, @NotNull PoseStack poseStack, float bodyRot, float scale) {
		if (this.isShaking(renderState)) {
			bodyRot += (float)(Math.cos((float)Mth.floor(renderState.ageInTicks) * 3.25F) * Math.PI * (double)0.4F);
		}

		if (!renderState.hasPose(Pose.SLEEPING)) {
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));
		}

		if (renderState.deathTime > 0.0F) {
			poseStack.scale(0,0,0);
		} else if (renderState.isAutoSpinAttack) {
			poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - renderState.xRot));
			poseStack.mulPose(Axis.YP.rotationDegrees(renderState.ageInTicks * -75.0F));
		} else if (renderState.isUpsideDown) {
			poseStack.translate(0.0F, (renderState.boundingBoxHeight + 0.1F) / scale, 0.0F);
			poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		}
	}
	@Override
	protected HumanoidModel.@NotNull ArmPose getArmPose(AbstractGem gem, @NotNull HumanoidArm arm) {
		if (gem.getMainArm() == arm && gem.isAggressive() && gem.getMainHandItem().is(item -> item.value() instanceof BowItem))
			return HumanoidModel.ArmPose.BOW_AND_ARROW;
		if (gem.getMainArm() == arm && gem.isAggressive() && gem.getMainHandItem().is(item -> item.value() instanceof CrossbowItem))
			return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
		return HumanoidModel.ArmPose.EMPTY;
	}
}
