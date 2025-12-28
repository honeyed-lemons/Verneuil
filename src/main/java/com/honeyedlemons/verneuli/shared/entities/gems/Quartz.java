package com.honeyedlemons.verneuli.shared.entities.gems;

import com.honeyedlemons.verneuli.shared.data.datatypes.GemVariant;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Quartz extends AbstractGem{
    public Quartz(EntityType<? extends Quartz> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    GemVariant DefaultGemTypeVariant() {
        return getGemVariant("amethyst");
    }
}
