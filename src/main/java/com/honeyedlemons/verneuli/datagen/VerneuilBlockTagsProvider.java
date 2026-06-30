package com.honeyedlemons.verneuli.datagen;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.blocks.VerneuilBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class VerneuilBlockTagsProvider extends BlockTagsProvider {
	public VerneuilBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, Verneuil.MODID);
	}
	@Override
	protected void addTags(HolderLookup.Provider lookupProvider) {
		for (var block : VerneuilBlocks.DRAINED_STONES.values()) {
			this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
		}
		for (var block : VerneuilBlocks.DRAINED_SOILS.values()) {
			this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block.get());
		}
		for (var block : VerneuilBlocks.DRAINED_DUSTS.values()) {
			this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block.get());
		}
	}
}
