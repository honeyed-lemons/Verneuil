package com.honeyedlemons.verneuli.shared.data.datatypes;

import com.honeyedlemons.verneuli.Verneuil;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;


public class VerneuilDataTypes {
    public static final ResourceKey<Registry<GemVariant>> GEM_VARIANT =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Verneuil.MODID,"gem_variant"));
}
