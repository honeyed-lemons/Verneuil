package com.honeyedlemons.verneuli.shared.data.dataAttachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public class GemAppearanceData {

	public Map<String,String> LayerData;
	public Map<String,Integer> ColorData;

	public GemAppearanceData(Map<String,String> layerData, Map<String,Integer> colorData){
		this.LayerData = layerData;
		this.ColorData = colorData;
	}

	public GemAppearanceData()
	{
		this.LayerData = new HashMap<>();
		this.ColorData = new HashMap<>();
	}

	public Map<String,String> getLayerData()
	{
		return this.LayerData;
	}
	public void setLayerData(Map<String,String> layerData)
	{
		this.LayerData = layerData;
	}

	public void addLayerData(String layerName, String layerVariant)
	{
		this.LayerData.put(layerName,layerVariant);
	}

	public Map<String,Integer> getColorData()
	{
		return this.ColorData;
	}

	public void setColorData(Map<String,Integer> colorData)
	{
		this.ColorData = colorData;
	}

	public void addColorData(String paletteName, Integer paletteValue)
	{
		this.ColorData.put(paletteName,paletteValue);
	}

	public static final Codec<GemAppearanceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(Codec.STRING,Codec.STRING).fieldOf("layer_data").forGetter(GemAppearanceData::getLayerData),
			Codec.unboundedMap(Codec.STRING,Codec.INT).fieldOf("color_data").forGetter(GemAppearanceData::getColorData)
	).apply(instance, GemAppearanceData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, GemAppearanceData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8),
			GemAppearanceData::getLayerData,
			ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT),
			GemAppearanceData::getColorData,
			GemAppearanceData::new
	);
}
