package com.honeyedlemons.verneuli.data.dataTypes;

import com.honeyedlemons.verneuli.Verneuil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;


public class VerneuilDataTypes {
    public static final ResourceKey<Registry<GemVariant>> GEM_VARIANT =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Verneuil.MODID,"gem_variant"));

    public static final ResourceKey<Registry<DefaultGemVariant>> DEFAULT_GEM_VARIANT =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Verneuil.MODID,"default_gem_variant"));
}
