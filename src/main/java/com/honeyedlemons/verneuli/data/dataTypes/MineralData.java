package com.honeyedlemons.verneuli.data.dataTypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;

public record MineralData(Map<String,Float> mineralComposition) {
	public static final Codec<MineralData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("mineral_composition").forGetter(MineralData::mineralComposition))
			.apply(instance, MineralData::new));

	public MineralData(MineralData mineralData) {
		this(mineralData.mineralComposition);
	}

	public static void addData(MineralData dataToAddto, MineralData dataToAdd)
	{
		dataToAdd.mineralComposition.forEach((key,value)->
		{
			var currentValue = dataToAddto.mineralComposition.getOrDefault(key,0f);
			currentValue += value;
			dataToAddto.mineralComposition.put(key,currentValue);
		});
	}
}
