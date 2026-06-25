package com.honeyedlemons.verneuli.util;

import com.honeyedlemons.verneuli.data.dataTypes.DefaultGemVariant;
import com.honeyedlemons.verneuli.data.dataTypes.GemVariant;
import com.honeyedlemons.verneuli.data.dataTypes.VerneuilRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class RegistryUtil {

	public static Registry<EntityType<?>> getEntityTypeRegistry(Level level)
	{
		return level.registryAccess().lookupOrThrow(Registries.ENTITY_TYPE);
	}

	public static Registry<GemVariant> getGemVariantRegistry(Level level)
	{
		return level.registryAccess().lookupOrThrow(VerneuilRegistries.GEM_VARIANT);
	}

	public static GemVariant getGemVariantFromRegistry(Level level, Identifier resourceLocation) {
		return getGemVariantRegistry(level).getValue(resourceLocation);
	}

	public static Registry<DefaultGemVariant> getDefaultGemVariantRegistry(Level level)
	{
		return level.registryAccess().lookupOrThrow(VerneuilRegistries.DEFAULT_GEM_VARIANT);
	}
}
