package com.honeyedlemons.verneuli.data.dataMaps;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.data.dataMaps.valueModifiers.GemVariantsMerger;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.List;

public class EntityGemVariantsDataMap {

	public record GemVariants(List<ResourceLocation> gemVariants) {}

	public static final Codec<GemVariants> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.list(ResourceLocation.CODEC).fieldOf("gem_variants").forGetter(GemVariants::gemVariants))
			.apply(instance, GemVariants::new));
	public static final AdvancedDataMapType<EntityType<?>, GemVariants, DataMapValueRemover.Default<GemVariants, EntityType<?>>> GEM_VARIANTS = AdvancedDataMapType
			.builder(ResourceLocation.fromNamespaceAndPath(Verneuil.MODID, "gem_variants"), Registries.ENTITY_TYPE, CODEC)
			.remover(DataMapValueRemover.Default.codec())
			.merger(new GemVariantsMerger())
			.build();

	@SubscribeEvent
	public static void register(RegisterDataMapTypesEvent event) {
		event.register(GEM_VARIANTS);
	}
}
