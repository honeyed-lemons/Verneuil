package com.honeyedlemons.verneuli.datagen;

import com.honeyedlemons.verneuli.blocks.InjectorBlockEntity;
import com.honeyedlemons.verneuli.blocks.TripleBlock;
import com.honeyedlemons.verneuli.blocks.TripleBlockSection;
import com.honeyedlemons.verneuli.blocks.VerneuilBlocks;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Set;

public class VerneuilBlockLootTableSubProvider extends BlockLootSubProvider {

	public VerneuilBlockLootTableSubProvider(HolderLookup.Provider lookupProvider) {
		// The first parameter is a set of blocks we are creating loot tables for. Instead of hardcoding,
		// we use our block registry and just pass an empty set here.
		// The second parameter is the feature flag set, this will be the default flags
		// unless you are adding custom flags (which is beyond the scope of this article).
		super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
	}
	// The contents of this Iterable are used for validation.
	// We return an Iterable over our block registry's values here.
	@Override
	protected Iterable<Block> getKnownBlocks() {
		var blocks = new java.util.ArrayList<>(VerneuilBlocks.BLOCKS.getEntries().stream().map(
				block -> (Block) block.value()).toList());
		blocks.remove(VerneuilBlocks.GEODE.get());
		return blocks;
	}

	@Override
	protected void generate() {
		for (DeferredBlock<?> block : VerneuilBlocks.DRAINED_STONES.values())
			this.add(block.get(),this.createSilkTouchOnlyTable(block.get()));
		for (DeferredBlock<?> block : VerneuilBlocks.DRAINED_SOILS.values())
			this.add(block.get(),this.createSilkTouchOnlyTable(block.get()));
		for (DeferredBlock<?> block : VerneuilBlocks.DRAINED_DUSTS.values())
			this.add(block.get(),this.createSilkTouchOnlyTable(block.get()));
		this.add(VerneuilBlocks.INJECTOR.get(), block ->
				this.createSinglePropConditionTable(block, TripleBlock.SECTION, TripleBlockSection.MIDDLE));
		this.add(VerneuilBlocks.INJECTOR.get(), createSinglePropConditionTable(VerneuilBlocks.INJECTOR.get(), InjectorBlockEntity.InjectorBlock.SECTION,TripleBlockSection.MIDDLE));
	}
}
