package com.honeyedlemons.verneuli.data.dataTypes;


import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public record CruxData(MineralData mineralData, float idealTemperature, float idealHeight) {

	public static final Codec<CruxData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MineralData.CODEC.fieldOf("ideal_ratio").forGetter(CruxData::mineralData),
			Codec.FLOAT.fieldOf("ideal_temperature").forGetter(CruxData::idealTemperature),
			Codec.FLOAT.fieldOf("ideal_height").forGetter(CruxData::idealHeight))
			.apply(instance, CruxData::new));

	/// Get the difference between two cruxes
	public static double getCruxSimilarity(CruxData crux, CruxData cruxToReference)
	{
		var ratioSimilarity = crux.getRatioSimilarity(cruxToReference);
		var heightSimilarity = crux.getHeightSimilarity(cruxToReference);
		var temperatureSimilarity = crux.getTemperatureSimilarity(cruxToReference);

		return (ratioSimilarity + heightSimilarity + temperatureSimilarity) / 3;
	}

	private float getRatioSimilarity(CruxData cruxToReference)
	{
		var mineralComposition = this.mineralData().mineralComposition();
		var referenceMineralComposition = cruxToReference.mineralData().mineralComposition();

		AtomicReference<List<Float>> differences = new AtomicReference<>(new ArrayList<>());

		mineralComposition.forEach((key, value) -> {
			var referenceValue = referenceMineralComposition.containsKey(key) ? referenceMineralComposition.get(key) : 0;
			var total = (Math.abs(value - referenceValue)/(value+referenceValue)/2);
			differences.get().add(total);
		});

		var total = new AtomicDouble();
		for (float difference : differences.get())
		{
			total.addAndGet(difference);
		}

		return total.floatValue() / differences.get().size();
	}

	private float getHeightSimilarity(CruxData cruxToReference)
	{
		var difference = Math.abs((this.idealHeight() + 64) - (cruxToReference.idealHeight() + 64));

		var average = (this.idealHeight() + cruxToReference.idealHeight() + 128) / 2;

		return difference / average;
	}

	private float getTemperatureSimilarity(CruxData cruxToReference)
	{
		var difference = Math.abs(this.idealTemperature() - cruxToReference.idealTemperature());

		var average = (this.idealTemperature() + cruxToReference.idealTemperature()) / 2;

		return difference / average;
	}
}
