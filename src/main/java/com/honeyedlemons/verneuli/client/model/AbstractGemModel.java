package com.honeyedlemons.verneuli.client.model;

import com.honeyedlemons.verneuli.client.renderer.entity.renderstates.GemRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;

import java.util.Set;
import java.util.function.Function;

public abstract class AbstractGemModel extends HumanoidModel<GemRenderState> {
    public AbstractGemModel(ModelPart root) {
        super(root);
    }
}
