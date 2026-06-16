package com.honeyedlemons.verneuli.blocks;

import com.honeyedlemons.verneuli.data.dataMaps.BlockDrainDataMap;
import com.honeyedlemons.verneuli.data.dataTypes.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GeodeBlockEntity extends BlockEntity {

	public GeodeBlockEntity(BlockPos pos, BlockState blockState) {
		super(VerneuilBlocks.GEODE_ENTITY.get(), pos, blockState);
	}

	private final MineralData geologicalData = new MineralData(new HashMap<>());

	public static class GeodeBlock extends Block implements EntityBlock {
		public GeodeBlock(Properties properties) {
			super(properties);
		}

		@Override
		public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
			return new GeodeBlockEntity(pos, state);
		}

		@Override
		protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
			if (!(level.getBlockEntity(pos) instanceof GeodeBlockEntity geode))
				return InteractionResult.FAIL;

			if (!(level instanceof ServerLevel serverLevel))
				return InteractionResult.FAIL;

			geode.drainBlock(pos.above(),serverLevel);
			var gemVariant = geode.getGemVariant();
			player.sendOverlayMessage(Component.literal(gemVariant.getFirst().gemItem().create().getDisplayName().getString()+" "+gemVariant.getSecond().toString()));
			return InteractionResult.SUCCESS;
		}

		public MineralData GetRatio(MineralData map) {
			MineralData mineralData = new MineralData(new HashMap<>());

			Float totalCount = 0f;

			for (Map.Entry<String, Float> entry : map.mineralComposition().entrySet())
				totalCount += entry.getValue();

			for (Map.Entry<String, Float> entry : map.mineralComposition().entrySet())
				mineralData.mineralComposition().put(entry.getKey(), entry.getValue() / totalCount);

			return mineralData;
		}
	}

	public float getTemperature(Level level)
	{
		return level.getBiome(this.getBlockPos()).value().getBaseTemperature();
	}

	public Pair<GemVariant, Double> getGemVariant()
	{
		if (this.getLevel() == null)
			return null;

		float temperature = getTemperature(this.getLevel());
		int yLevel = this.getBlockPos().getY();
		CruxData cruxData = new CruxData(this.geologicalData, temperature, yLevel);

		Registry<GemVariant> gemVariantRegistry = this.getLevel().registryAccess().lookupOrThrow(VerneuilDataTypes.GEM_VARIANT);
		var gemVariantEntryset = gemVariantRegistry.entrySet();

		// TEMP
		double minDifference = Double.MAX_VALUE;

		GemVariant closestVariant = null;

		double finalDifference = 0;

		for (Map.Entry<ResourceKey<GemVariant>, GemVariant> entry : gemVariantEntryset) {
			if (entry.getValue().crux().isPresent()) {
				double difference = CruxData.getCruxSimilarity(entry.getValue().crux().get(), cruxData);
				if (difference <= minDifference) {
					finalDifference = difference;
					closestVariant = entry.getValue();
				}
			}
		}

		return new Pair<>(closestVariant, finalDifference);
	}

	public void drainBlock(BlockPos pos, ServerLevel serverLevel)
	{
		Holder<Block> holder = serverLevel.getBlockState(pos).typeHolder();
		BlockDrainData blockDrainData = holder.getData(BlockDrainDataMap.BLOCK_DRAIN_DATA);

		if (blockDrainData == null)
			return;

		MineralData.addData(this.geologicalData, blockDrainData.mineralData());

		if (blockDrainData.drainTo().isPresent())
			serverLevel.setBlockAndUpdate(pos, blockDrainData.drainTo().get().defaultBlockState());
	}

	@Override
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		input.read("geo_data", MineralData.CODEC).ifPresent(MineralData::new);
	}

	@Override
	public void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.store("geo_data", MineralData.CODEC, this.geologicalData);
	}
}
