package com.honeyedlemons.verneuli.data.dataTypes;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CruxData(MineralData mineralData, float idealTemperature, float idealHeight) {

	public static final Codec<CruxData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MineralData.CODEC.fieldOf("ideal_ratio").forGetter(CruxData::mineralData),
			Codec.FLOAT.fieldOf("ideal_temperature").forGetter(CruxData::idealTemperature),
			Codec.FLOAT.fieldOf("ideal_height").forGetter(CruxData::idealHeight))
			.apply(instance, CruxData::new));

	public static double getSimilarity(CruxData crux1, CruxData crux2)
	{
		return 0;
	}
}
