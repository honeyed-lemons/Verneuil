package com.honeyedlemons.verneuli.blocks;

import com.google.common.collect.Iterables;
import com.honeyedlemons.verneuli.config.VerneuilConfigServer;
import com.honeyedlemons.verneuli.data.dataMaps.BlockDrainDataMap;
import com.honeyedlemons.verneuli.data.dataTypes.*;
import com.honeyedlemons.verneuli.util.CruxUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashMap;
import java.util.Map;

public class GeodeBlockEntity extends BlockEntity {

	public GeodeBlockEntity(BlockPos pos, BlockState blockState) {
		super(VerneuilBlocks.GEODE_ENTITY.get(), pos, blockState);
	}

	private final MineralData geologicalData = new MineralData(new HashMap<>());

	public static class GeodeBlock extends Block implements EntityBlock {

		public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

		public GeodeBlock(Properties properties) {
			super(properties);
			this.registerDefaultState(this.stateDefinition.any().setValue(AGE,0));
		}

		@Override
		protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(AGE);
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

		protected boolean isRandomlyTicking(BlockState state) {
			return this.getAge(state) <= getMaxAge();
		}
		protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
			int age = this.getAge(state);
			int incubationChance = VerneuilConfigServer.CONFIG.incubationChance.get();

			if (!level.isAreaLoaded(pos, 1))
				return;

			// Drain a block in an area
			if ((level.getBlockEntity(pos) instanceof GeodeBlockEntity geode)) {
				for (int i = 0; i < ((age + 1) * 8); i++) {
					geode.drainInArea(level, pos, getAge(state));
				}
			}

			if (random.nextInt((incubationChance)) == 0) {
				if (age < this.getMaxAge()) {
					level.setBlockAndUpdate(pos, this.getStateForAge(age + 1));
				}
				else {
					if ((level.getBlockEntity(pos) instanceof GeodeBlockEntity geode))
						geode.emerge();
				}
			}
		}

		public BlockState getStateForAge(int age) {
			return this.defaultBlockState().setValue(this.getAgeProperty(), age);
		}

		protected int getMaxAge() {
			return 3;
		}
		protected IntegerProperty getAgeProperty() {
			return AGE;
		}
		public int getAge(BlockState state) {
			return state.getValue(this.getAgeProperty());
		}
	}

	private void drainInArea(ServerLevel level, BlockPos pos, int age) {
		// Get a iterable list of blockposes in an area around the given position, it'll infl
		var inflation = (age * 3);

		var blocksToIterate = BlockPos.betweenClosed(AABB.of(new BoundingBox(pos)).inflate(inflation));

		var randomBlock = Iterables.get(blocksToIterate, level.getRandom().nextInt(Iterables.size(blocksToIterate)));

		drainBlock(randomBlock, level);
	}

	private void emerge() {

	}

	public Pair<GemVariant, Double> getGemVariant()
	{
		if (this.getLevel() == null)
			return null;

		float temperature = CruxUtil.getTemperature(this.getLevel(), this.getBlockPos());

		int yLevel = this.getBlockPos().getY();
		CruxData cruxData = new CruxData(this.geologicalData, temperature, yLevel);

		Registry<GemVariant> gemVariantRegistry = this.getLevel().registryAccess().lookupOrThrow(VerneuilDataTypes.GEM_VARIANT);
		var gemVariantEntryset = gemVariantRegistry.entrySet();

		// TEMP
		double minDifference = Double.MAX_VALUE;

		GemVariant closestVariant = null;

		double finalDifference = 0;

		for (Map.Entry<ResourceKey<GemVariant>, GemVariant> entry : gemVariantEntryset) {
			if (entry.getValue().crux().isEmpty())
				continue;

			double difference = CruxData.getCruxSimilarity(entry.getValue().crux().get(), cruxData);

			if (!(difference <= minDifference) || (finalDifference != 0 && !(difference <= finalDifference)))
				continue;

			finalDifference = difference;
			closestVariant = entry.getValue();
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

		if (blockDrainData.drainTo().isPresent()) {
			var state = CruxUtil.getDrainedBlockstate(serverLevel.getLevel(),pos,blockDrainData.drainTo().get().defaultBlockState());
			serverLevel.setBlockAndUpdate(pos, state);
		}
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
