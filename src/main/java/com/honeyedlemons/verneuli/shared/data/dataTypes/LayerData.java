package com.honeyedlemons.verneuli.shared.data.dataTypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public final record LayerData(String layerName, Optional<String> paletteName, Boolean isVariant) {

	public static final Codec<LayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(LayerData::layerName),
			Codec.STRING.optionalFieldOf("palette").forGetter(LayerData::paletteName),
			Codec.BOOL.fieldOf("is_variant").forGetter(LayerData::isVariant)
	).apply(instance, LayerData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, LayerData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			LayerData::layerName,
			ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
			LayerData::paletteName,
			ByteBufCodecs.BOOL,
			LayerData::isVariant,
			LayerData::new
	);

}
