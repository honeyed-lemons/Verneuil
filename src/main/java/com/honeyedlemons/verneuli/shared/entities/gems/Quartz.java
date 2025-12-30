package com.honeyedlemons.verneuli.shared.entities.gems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Quartz extends AbstractGem{
    public Quartz(EntityType<? extends Quartz> entityType, Level level) {
        super(entityType, level);
    }
}
