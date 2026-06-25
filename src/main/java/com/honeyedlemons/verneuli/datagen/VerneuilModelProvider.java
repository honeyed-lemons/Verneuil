package com.honeyedlemons.verneuli.datagen;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.blocks.BlockIndexed;
import com.honeyedlemons.verneuli.blocks.VerneuilBlocks;
import com.honeyedlemons.verneuli.items.GemItem;
import com.honeyedlemons.verneuli.items.VerneuilItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.*;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.createRotatedVariants;

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
		for (DeferredBlock<?> block : VerneuilBlocks.DRAINED_STONES.values())
		{
			List<MultiVariant> variants = new ArrayList<>();

			for (int i = 0; i <= 2; i++)
			{
				var indexSuffixForModel = i == 0 ? "" : "_" + i;
				var topDark = TextureMapping.getBlockTexture(block.get(),"_top_dark");
				var topLight = TextureMapping.getBlockTexture(block.get(),"_top");
				var side = TextureMapping.getBlockTexture(block.get(),"_"+i);
				var topMaterial = i == 2 ? topDark : topLight;
				TextureMapping textureMapping = new TextureMapping().put(TextureSlot.SIDE,side).put(TextureSlot.END,topMaterial);
				variants.add(i,BlockModelGenerators.plainVariant(ModelTemplates.CUBE_COLUMN.create(ModelLocationUtils.getModelLocation(block.get(), indexSuffixForModel),textureMapping, blockModels.modelOutput)));
			}

			// Add one or multiple models based on the block state properties
			blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.get()).with(createIntegerModelDispatch(BlockIndexed.INDEX,variants)));
		}

		List<DeferredBlock<?>> blocksThatRandomRotate = new ArrayList<>();
		blocksThatRandomRotate.addAll(VerneuilBlocks.DRAINED_DUSTS.values());
		blocksThatRandomRotate.addAll(VerneuilBlocks.DRAINED_SOILS.values());

		for (DeferredBlock<?> block : blocksThatRandomRotate)
		{
			Variant normal = BlockModelGenerators.plainModel(TexturedModel.CUBE.create(block.get(), blockModels.modelOutput));
			blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.get(), createRotatedVariants(normal)));
		}
	}

	public static PropertyDispatch<MultiVariant> createIntegerModelDispatch(IntegerProperty property, List<MultiVariant> variants) {

		var dispatch = PropertyDispatch.initial(property);

		for (int i = 0; i < variants.size(); i++) {
			dispatch = dispatch.select(i, variants.get(i));
		}

		return dispatch;
	}

	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		final Set<ResourceKey<Block>> manuallyGeneratedBlockStates = new HashSet<>();

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
