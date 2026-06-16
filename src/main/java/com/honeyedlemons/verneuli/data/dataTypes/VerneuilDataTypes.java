package com.honeyedlemons.verneuli.data.dataTypes;

import com.honeyedlemons.verneuli.Verneuil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;


public class VerneuilDataTypes {
	public static final ResourceKey<Registry<GemVariant>> GEM_VARIANT = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(Verneuil.MODID, "gem_variant"));

	public static final ResourceKey<Registry<DefaultGemVariant>> DEFAULT_GEM_VARIANT = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(Verneuil.MODID, "default_gem_variant"));
}
