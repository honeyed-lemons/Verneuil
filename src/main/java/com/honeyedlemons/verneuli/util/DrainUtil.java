package com.honeyedlemons.verneuli.util;

import com.google.common.collect.Iterables;
import com.honeyedlemons.verneuli.blocks.BlockIndexed;
import com.honeyedlemons.verneuli.blocks.GeodeBlockEntity;
import com.honeyedlemons.verneuli.blocks.VerneuilBlocks;
import com.honeyedlemons.verneuli.compat.OpenPartiesAndClaimsCompat;
import com.honeyedlemons.verneuli.data.dataMaps.BlockDrainDataMap;
import com.honeyedlemons.verneuli.data.dataTypes.BlockDrainData;
import com.honeyedlemons.verneuli.data.dataTypes.CruxData;
import com.honeyedlemons.verneuli.data.dataTypes.MineralData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

public class DrainUtil {

	public enum DrainType implements StringRepresentable{
		STONE("stone"),SOIL("soil"),DUST("dust"),LOG("log");

		public static final StringRepresentable.EnumCodec<DrainType> CODEC = StringRepresentable.fromEnum(DrainType::values);

		private final String name;

		DrainType(String name) {
			this.name = name;
		}

		public String toString() {
			return this.getSerializedName();
		}

		public String getSerializedName() {
			return this.name;
		}
	}
	public enum DrainedColor implements StringRepresentable {
		Purple("purple"), Red("red"), Blue("blue"), Black("black");

		private final String name;

		DrainedColor(String name) {
			this.name = name;
		}

		public String toString() {
			return this.getSerializedName();
		}

		public String getSerializedName() {
			return this.name;
		}
	}

	public static float getTemperature(Level level, BlockPos pos)
	{
		return level.getBiome(pos).value().getBaseTemperature();
	}

	public static void drainInArea(GeodeBlockEntity geode, ServerLevel level, BlockPos pos, int age) {
		// Get a iterable list of block positions in an area around the given position, it'll infl
		var inflation = (age * 3);

		var blocksToIterate = BlockPos.betweenClosed(AABB.of(new BoundingBox(pos)).inflate(inflation));

		var randomBlock = Iterables.get(blocksToIterate, level.getRandom().nextInt(Iterables.size(blocksToIterate)));

		drainBlock(geode, randomBlock, level);
	}

	public static void drainBlock(GeodeBlockEntity geode, BlockPos pos, ServerLevel serverLevel)
	{
		if (!serverLevel.isAreaLoaded(pos, 1))
			return;

		if (ModList.get().isLoaded("openpartiesandclaims")) {
			serverLevel.getLevel();
			if (OpenPartiesAndClaimsCompat.canBreakBlock(serverLevel.getLevel(), EntityReference.getLivingEntity(geode.getOwnedBy(), serverLevel.getLevel()),pos))
				return;
		}


		Holder<Block> holder = serverLevel.getBlockState(pos).typeHolder();
		BlockDrainData blockDrainData = holder.getData(BlockDrainDataMap.BLOCK_DRAIN_DATA);

		if (blockDrainData == null)
			return;

		MineralData.addData(geode.getMineralData(), blockDrainData.mineralData());

		var state = Blocks.AIR.defaultBlockState();

		if (blockDrainData.drainTo().isPresent()) {
			var drainedColor = getDrainedColor(serverLevel,pos);
			var block = getDrainedBlock(drainedColor,blockDrainData.drainTo().get());
			state = getDrainedBlockState(serverLevel,pos,block.defaultBlockState());
		}

		serverLevel.setBlockAndUpdate(pos, state);
	}

	public static CruxData getCruxData(Level level, GeodeBlockEntity geode)
	{
		float temperature = DrainUtil.getTemperature(level, geode.getBlockPos());
		int yLevel = geode.getBlockPos().getY();
		return new CruxData(geode.getMineralData(), temperature, yLevel);
	}

	private static Block getDrainedBlock(DrainedColor color, DrainType type)
	{
		if (type == DrainType.STONE)
			return VerneuilBlocks.DRAINED_STONES.get(color).get();
		if (type == DrainType.SOIL)
			return VerneuilBlocks.DRAINED_SOILS.get(color).get();
		if (type == DrainType.DUST)
			return VerneuilBlocks.DRAINED_DUSTS.get(color).get();
		else
			return Blocks.AIR; // TEMP
	}

	private static BlockState getDrainedBlockState(Level level, BlockPos blockPos, BlockState blockState)
	{
		var blockType = blockState.getBlock();

		if (blockState.hasProperty(BlockIndexed.INDEX))
			blockState = blockState.setValue(BlockIndexed.INDEX,getDrainedIndex(level,blockPos));

		return blockState;
	}

	public static DrainedColor getDrainedColor(Level level, BlockPos blockPos) {
		var temperature = getTemperature(level, blockPos);

		if (blockPos.getY() <= 0)
			return DrainedColor.Black;
		else if (temperature <= 0.25)
			return DrainUtil.DrainedColor.Blue;
		else if (temperature >= 0.95)
			return DrainedColor.Red;
		else
			return DrainedColor.Purple;
	}

	public static int getDrainedIndex(Level level, BlockPos blockPos)
	{
		return Math.abs(blockPos.getY() % 3);
	}

	/// Finds the closest air block, searches in each cardinal direction.
	public static @Nullable BlockPos findClosestAir(Level level, BlockPos pos) {
		final int maxDistance = 16;

		// Essentially, what this does is check the four closest blocks in each cardinal directions
		// at each distance, returning as soon as it can find an air block.
		for (int d = 1; d <= maxDistance; d++) {
			BlockPos[] candidates = {
					pos.offset( d, 0, 0),
					pos.offset(-d, 0, 0),
					pos.offset(0, 0,  d),
					pos.offset(0, 0, -d)
			};

			for (BlockPos candidate : candidates) {
				var candidateState = level.getBlockState(candidate);

				if (!candidateState.isAir())
					continue;

				if (level.canSeeSky(candidate))
					return candidate;

				if (level.getBlockState(candidate.above(5)).isAir())
					return candidate;
			}
		}

		return null;
	}
}
