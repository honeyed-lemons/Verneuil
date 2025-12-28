package com.honeyedlemons.verneuli.client.model;


import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class QuartzModel extends AbstractGemModel {
    public QuartzModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();


        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(16, 30).addBox(-4.25F, -1.75F, -2.25F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition right_arm2 = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 30).addBox(0.25F, -1.75F, -2.25F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 49).addBox(-4.5F, -0.25F, -2.25F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 13.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 49).addBox(0.5F, -0.25F, -2.25F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 13.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F))
                .texOffs(33, 39).addBox(-4.0F, -6.0F, -5.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.26F)), PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(28, 43).addBox(-6.5F, -6.25F, -2.25F, 13.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(4, 16).addBox(-2.5F, -9.75F, -6.25F, 5.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 16).addBox(-5.0F, -6.0F, -3.0F, 10.0F, 13.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}