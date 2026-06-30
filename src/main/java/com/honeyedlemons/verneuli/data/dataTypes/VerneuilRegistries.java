package com.honeyedlemons.verneuli.data.dataTypes;

import com.honeyedlemons.verneuli.Verneuil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;


public class VerneuilRegistries {
	public static final ResourceKey<Registry<GemVariant>> GEM_VARIANT = ResourceKey.createRegistryKey(Verneuil.id("gem_variant"));

	public static final ResourceKey<Registry<DefaultGemVariant>> DEFAULT_GEM_VARIANT = ResourceKey.createRegistryKey(Verneuil.id("default_gem_variant"));
}
