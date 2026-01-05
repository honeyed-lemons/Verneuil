package com.honeyedlemons.verneuli.entities.gems;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Quartz extends AbstractGem{
    public Quartz(EntityType<? extends Quartz> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    boolean canWearArmor() {
        return true;
    }

    @Override
    public Boolean canFight() {
        return true;
    }


    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MAX_HEALTH,30)
                .add(Attributes.ATTACK_DAMAGE, 1.5f)
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.ARMOR,5)
                .add(Attributes.ARMOR_TOUGHNESS,2);
    }
}
