package com.honeyedlemons.verneuli.datagen;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.blocks.VerneuilBlocks;
import com.honeyedlemons.verneuli.items.GemItem;
import com.honeyedlemons.verneuli.items.VerneuilItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@EventBusSubscriber(modid = Verneuil.MODID)
public class VerneuilModelProvider extends ModelProvider {
	public VerneuilModelProvider(PackOutput output) {
		super(output, Verneuil.MODID);
	}


	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		// register gem items
		for (GemItem gem : VerneuilItems.gems()) {
			itemModels.generateFlatItem(gem, ModelTemplates.FLAT_ITEM);
		}
	}

	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		final Set<ResourceKey<Block>> manuallyGeneratedBlockStates = new HashSet<>();

		manuallyGeneratedBlockStates.add(VerneuilBlocks.DRAINED_STONE.getKey());
		manuallyGeneratedBlockStates.add(VerneuilBlocks.DRAINED_SOIL.getKey());
		manuallyGeneratedBlockStates.add(VerneuilBlocks.DRAINED_DUST.getKey());
		manuallyGeneratedBlockStates.add(VerneuilBlocks.GEODE.getKey());

		return super.getKnownBlocks().filter(holder -> !manuallyGeneratedBlockStates.contains(holder.getKey()));
	}

	public ModelTemplate gemTemplate(GemItem item) {
		return new ModelTemplate(Optional.of(getGemModelLocation(item)), Optional.empty(), TextureSlot.LAYER0);
	}

	public static Identifier getGemModelLocation(GemItem item) {
		Identifier identifier = BuiltInRegistries.ITEM.getKey(item);
		return identifier.withPrefix("item/gems/" + item.entityType.toShortString() + "/");
	}

	@SubscribeEvent // on the mod event bus
	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider(VerneuilModelProvider::new);
	}
}
