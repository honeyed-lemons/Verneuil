package com.honeyedlemons.verneuli.shared.data.datamaps;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.shared.data.datatypes.GemVariant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.ArrayList;
import java.util.List;

public class EntityGemVariantsDataMap {

    public record GemVariants(List<ResourceLocation> gemVariants){}
    public static final Codec<GemVariants> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(ResourceLocation.CODEC).fieldOf("gem_variants").forGetter(GemVariants::gemVariants)
    ).apply(instance, GemVariants::new));
    public static final DataMapType<EntityType<?>, GemVariants> GEM_VARIANTS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(Verneuil.MODID, "gem_variants"),
            Registries.ENTITY_TYPE,
            CODEC
    ).build();

    @SubscribeEvent
    public static void register(RegisterDataMapTypesEvent event) {
        event.register(GEM_VARIANTS);
    }
}
