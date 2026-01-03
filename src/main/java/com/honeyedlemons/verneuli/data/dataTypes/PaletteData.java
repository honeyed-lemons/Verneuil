package com.honeyedlemons.verneuli.data.dataTypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public final record PaletteData(String paletteName, Optional<Integer> defaultColor, Boolean random) {

	public static final Codec<PaletteData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(PaletteData::paletteName),
			Codec.INT.optionalFieldOf("default_color").forGetter(PaletteData::defaultColor),
			Codec.BOOL.fieldOf("random").forGetter(PaletteData::random)
	).apply(instance, PaletteData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PaletteData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			PaletteData::paletteName,
			ByteBufCodecs.optional(ByteBufCodecs.INT),
			PaletteData::defaultColor,
			ByteBufCodecs.BOOL,
			PaletteData::random,
			PaletteData::new
	);

}
