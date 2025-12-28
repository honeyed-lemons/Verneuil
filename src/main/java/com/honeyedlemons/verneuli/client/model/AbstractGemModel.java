package com.honeyedlemons.verneuli.client.model;

import com.honeyedlemons.verneuli.client.renderer.entity.renderstates.GemRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractGemModel extends HumanoidModel<GemRenderState> {
    public AbstractGemModel(ModelPart root) {
        super(root);
    }
}
