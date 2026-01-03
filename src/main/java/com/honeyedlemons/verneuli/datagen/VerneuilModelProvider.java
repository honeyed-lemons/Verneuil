package com.honeyedlemons.verneuli.datagen;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.blocks.VerneuilBlocks;
import com.honeyedlemons.verneuli.items.GemItem;
import com.honeyedlemons.verneuli.items.VerneuilItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EventBusSubscriber(modid = Verneuil.MODID)
public class VerneuilModelProvider extends ModelProvider {
	public VerneuilModelProvider(PackOutput output) {
		super(output, Verneuil.MODID);
	}

	@Override
	protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
		// register gem items
		for(GemItem gem : VerneuilItems.gems())
		{
			itemModels.generateFlatItem(gem, ModelTemplates.FLAT_ITEM);
		}
		for (Block block : VerneuilBlocks.blocks())
		{
			blockModels.createTrivialCube(block);
		}
	}

	public ModelTemplate gemTemplate(GemItem item)
	{
		return new ModelTemplate(Optional.of(getGemModelLocation(item)), Optional.empty(), TextureSlot.LAYER0);
	}
	public static ResourceLocation getGemModelLocation(GemItem item) {
		ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(item);
		return resourcelocation.withPrefix("item/gems/"+item.entityType.toShortString()+"/");
	}
	@SubscribeEvent // on the mod event bus
	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider(VerneuilModelProvider::new);
	}
}
