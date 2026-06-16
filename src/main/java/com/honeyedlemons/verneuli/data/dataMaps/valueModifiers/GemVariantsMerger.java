package com.honeyedlemons.verneuli.data.dataMaps.valueModifiers;

import com.honeyedlemons.verneuli.data.dataMaps.EntityGemVariantsDataMap;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;

import java.util.ArrayList;
import java.util.List;

public class GemVariantsMerger implements DataMapValueMerger<EntityType<?>, EntityGemVariantsDataMap.GemVariants> {

	@Override
	public EntityGemVariantsDataMap.GemVariants merge(Registry<EntityType<?>> registry, Either<TagKey<EntityType<?>>, ResourceKey<EntityType<?>>> either1, EntityGemVariantsDataMap.GemVariants list1, Either<TagKey<EntityType<?>>, ResourceKey<EntityType<?>>> either2, EntityGemVariantsDataMap.GemVariants list2) {
		final List<Identifier> merged = new ArrayList<>();
		merged.addAll(list1.gemVariants());
		merged.addAll(list2.gemVariants());
		return new EntityGemVariantsDataMap.GemVariants(merged);
	}
}
