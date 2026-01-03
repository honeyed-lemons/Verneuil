package com.honeyedlemons.verneuli.entities.gems;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

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
}
