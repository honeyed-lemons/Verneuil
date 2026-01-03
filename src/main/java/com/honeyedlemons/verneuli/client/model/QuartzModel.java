package com.honeyedlemons.verneuli.client.model;


import com.honeyedlemons.verneuli.client.renderer.entity.renderstates.GemRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Function;

public class QuartzModel extends AbstractGemModel {
    public QuartzModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();


        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(16, 30).addBox(-3.25F, -1.75F, -2.25F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-6.0F, 2.0F, 0.0F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 30).addBox(-0.75F, -1.75F, -2.25F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(6.0F, 2.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 49).addBox(-4.5F, -0.25F, -2.25F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 13.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 49).addBox(0.5F, -0.25F, -2.25F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 13.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(33, 39).addBox(-4.0F, -6.0F, -5.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.26F))
                .texOffs(28, 43).addBox(-6.5F, -6.25F, -2.25F, 13.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(4, 16).addBox(-2.5F, -9.75F, -6.25F, 5.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 16).addBox(-5.0F, -6.0F, -3.0F, 10.0F, 13.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
    @Override
    public void setupAnim(GemRenderState renderState) {
        this.hat.visible = renderState.headEquipment.isEmpty();
        super.setupAnim(renderState);
    }

    public static ArmorModelSet<MeshDefinition> createArmorMeshSet(@NotNull CubeDeformation innerArmorCubeDeformation, @NotNull CubeDeformation outerArmorCubeDeformation) {
        return createArmorMeshSet(QuartzModel::createBaseArmorMesh, innerArmorCubeDeformation, outerArmorCubeDeformation);
    }

    public static ArmorModelSet<MeshDefinition> createArmorMeshSet(Function<CubeDeformation, MeshDefinition> meshCreator, @NotNull CubeDeformation innerCubeDeformation, @NotNull CubeDeformation outerCubeDeformation) {
        MeshDefinition meshdefinition = meshCreator.apply(outerCubeDeformation);
        meshdefinition.getRoot().retainPartsAndChildren(Set.of("head"));
        MeshDefinition meshdefinition1 = meshCreator.apply(outerCubeDeformation);
        meshdefinition1.getRoot().retainExactParts(Set.of("body"));
        meshdefinition1.getRoot().addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.25F, -1.75F, -2.25F, 4.0F, 14.0F, 4.0F, new CubeDeformation(1.25f)), PartPose.offset(-6.0F, 2.0F, 0.0F));
        meshdefinition1.getRoot().addOrReplaceChild("left_arm", CubeListBuilder.create().mirror().texOffs(40, 16).addBox(-0.75F, -1.75F, -2.25F, 4.0F, 14.0F, 4.0F, new CubeDeformation(1.25F)), PartPose.offset(6.0F, 2.0F, 0.0F));
        MeshDefinition meshdefinition2 = meshCreator.apply(innerCubeDeformation);
        meshdefinition2.getRoot().retainExactParts(Set.of("left_leg", "right_leg", "body"));
        MeshDefinition meshdefinition3 = meshCreator.apply(outerCubeDeformation);
        meshdefinition3.getRoot().retainExactParts(Set.of("left_leg", "right_leg"));
        return new ArmorModelSet<>(meshdefinition, meshdefinition1, meshdefinition2, meshdefinition3);
    }

    public static MeshDefinition createBaseArmorMesh(CubeDeformation cubeDeformation) {
        MeshDefinition meshdefinition = createMesh(cubeDeformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(-0.1F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(-0.1F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        return meshdefinition;
    }
}