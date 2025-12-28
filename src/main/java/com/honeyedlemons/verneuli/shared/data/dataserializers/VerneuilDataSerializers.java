package com.honeyedlemons.verneuli.shared.data.dataserializers;

import com.honeyedlemons.verneuli.shared.data.datatypes.GemVariant;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;
import java.util.function.Supplier;

import static com.honeyedlemons.verneuli.Verneuil.MODID;

public class VerneuilDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, MODID);

    public static Supplier<EntityDataSerializer<Map<String,Integer>>> GEM_COLORS = DATA_SERIALIZERS.register(
            "gem_colors", () -> GemColorDataSerializer.GEM_COLOR);

    public static Supplier<EntityDataSerializer<Map<String,String>>> GEM_LAYER_VARIANTS = DATA_SERIALIZERS.register(
            "gem_layer_variants", () -> GemLayerVariantDataSerializer.GEM_LAYER_VARIANTS);

    public static Supplier<EntityDataSerializer<GemVariant>> GEM_VARIANT = DATA_SERIALIZERS.register(
            "gem_variant", () -> GemVariant.GEM_VARIANT);
}
