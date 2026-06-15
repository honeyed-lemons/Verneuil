package com.honeyedlemons.verneuli.data.dataTypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public record BlockDrainData(MineralData mineralData, Optional<Block> drainTo) {

	public static final Codec<BlockDrainData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					MineralData.CODEC.fieldOf("mineral_data").forGetter(BlockDrainData::mineralData),
					BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("drain_to").forGetter(BlockDrainData::drainTo))
			.apply(instance, BlockDrainData::new));
}
