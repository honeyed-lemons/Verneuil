package com.honeyedlemons.verneuli.datagen;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.blocks.BlockIndexed;
import com.honeyedlemons.verneuli.blocks.InjectorBlockEntity;
import com.honeyedlemons.verneuli.blocks.TripleBlockSection;
import com.honeyedlemons.verneuli.blocks.VerneuilBlocks;
import com.honeyedlemons.verneuli.items.GemItem;
import com.honeyedlemons.verneuli.items.VerneuilItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.*;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

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

		itemModels.generateFlatItem(VerneuilItems.GEM_SEED.asItem(),ModelTemplates.FLAT_ITEM);
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

		createInjectorBlockStates(VerneuilBlocks.INJECTOR.get(), blockModels);

		List<DeferredBlock<?>> blocksThatRandomRotate = new ArrayList<>();
		blocksThatRandomRotate.addAll(VerneuilBlocks.DRAINED_DUSTS.values());
		blocksThatRandomRotate.addAll(VerneuilBlocks.DRAINED_SOILS.values());

		for (DeferredBlock<?> block : blocksThatRandomRotate)
		{
			Variant normal = BlockModelGenerators.plainModel(TexturedModel.CUBE.create(block.get(), blockModels.modelOutput));
			blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block.get(), createRotatedVariants(normal)));
		}
	}

	public void createInjectorBlockStates(Block block, BlockModelGenerators blockModels)
	{
		blockModels.blockStateOutput.accept(createInjector(
				block,
				existingModel(Verneuil.id("block/injector_lower")),
				existingModel(Verneuil.id("block/injector_middle")),
				existingModel(Verneuil.id("block/injector_middle_full")),
				existingModel(Verneuil.id("block/injector_upper"))));
	}

	public static BlockModelDefinitionGenerator createInjector(
			Block block,
			MultiVariant lower,
			MultiVariant middle,
			MultiVariant middleFull,
			MultiVariant upper
	) {
		return MultiVariantGenerator.dispatch(block)
				.with(
						PropertyDispatch.initial(
								InjectorBlockEntity.InjectorBlock.SECTION,
								InjectorBlockEntity.InjectorBlock.FACING,
								InjectorBlockEntity.InjectorBlock.FULL
						)
								.select(TripleBlockSection.LOWER,Direction.NORTH,false,lower)
								.select(TripleBlockSection.LOWER,Direction.EAST,false,lower.with(Y_ROT_90))
								.select(TripleBlockSection.LOWER,Direction.WEST,false,lower.with(Y_ROT_270))
								.select(TripleBlockSection.LOWER,Direction.SOUTH,false,lower.with(Y_ROT_180))
								.select(TripleBlockSection.LOWER,Direction.NORTH,true,lower)
								.select(TripleBlockSection.LOWER,Direction.EAST,true,lower.with(Y_ROT_90))
								.select(TripleBlockSection.LOWER,Direction.WEST,true,lower.with(Y_ROT_270))
								.select(TripleBlockSection.LOWER,Direction.SOUTH,true,lower.with(Y_ROT_180))
								.select(TripleBlockSection.MIDDLE,Direction.NORTH,false,middle)
								.select(TripleBlockSection.MIDDLE,Direction.EAST,false,middle.with(Y_ROT_90))
								.select(TripleBlockSection.MIDDLE,Direction.WEST,false,middle.with(Y_ROT_270))
								.select(TripleBlockSection.MIDDLE,Direction.SOUTH,false,middle.with(Y_ROT_180))
								.select(TripleBlockSection.MIDDLE,Direction.NORTH,true,middleFull)
								.select(TripleBlockSection.MIDDLE,Direction.EAST,true,middleFull.with(Y_ROT_90))
								.select(TripleBlockSection.MIDDLE,Direction.WEST,true,middleFull.with(Y_ROT_270))
								.select(TripleBlockSection.MIDDLE,Direction.SOUTH,true,middleFull.with(Y_ROT_180))
								.select(TripleBlockSection.UPPER,Direction.NORTH,false,upper)
								.select(TripleBlockSection.UPPER,Direction.EAST,false,upper.with(Y_ROT_90))
								.select(TripleBlockSection.UPPER,Direction.WEST,false,upper.with(Y_ROT_270))
								.select(TripleBlockSection.UPPER,Direction.SOUTH,false,upper.with(Y_ROT_180))
								.select(TripleBlockSection.UPPER,Direction.NORTH,true,upper)
								.select(TripleBlockSection.UPPER,Direction.EAST,true,upper.with(Y_ROT_90))
								.select(TripleBlockSection.UPPER,Direction.WEST,true,upper.with(Y_ROT_270))
								.select(TripleBlockSection.UPPER,Direction.SOUTH,true,upper.with(Y_ROT_180))
				);
	}

	public static PropertyDispatch<MultiVariant> createIntegerModelDispatch(IntegerProperty property, List<MultiVariant> variants) {

		var dispatch = PropertyDispatch.initial(property);

		for (int i = 0; i < variants.size(); i++) {
			dispatch = dispatch.select(i, variants.get(i));
		}

		return dispatch;
	}

	public MultiVariant existingModel(Identifier identifier)
	{
		var variant = new Variant(identifier);
		return new MultiVariant(WeightedList.of(variant));
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
}
