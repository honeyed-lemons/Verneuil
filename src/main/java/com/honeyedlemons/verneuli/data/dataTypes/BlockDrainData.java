package com.honeyedlemons.verneuli.data.dataTypes;

import com.honeyedlemons.verneuli.util.DrainUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record BlockDrainData(MineralData mineralData, Optional<DrainUtil.DrainType> drainTo) {

	public static final Codec<BlockDrainData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					MineralData.CODEC.fieldOf("mineral_data").forGetter(BlockDrainData::mineralData),
					DrainUtil.DrainType.CODEC.optionalFieldOf("drain_to").forGetter(BlockDrainData::drainTo))
			.apply(instance, BlockDrainData::new));
}
