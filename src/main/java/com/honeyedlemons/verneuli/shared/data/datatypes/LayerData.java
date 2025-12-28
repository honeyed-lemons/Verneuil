package com.honeyedlemons.verneuli.shared.data.datatypes;

import com.honeyedlemons.verneuli.shared.data.datamaps.BlockMineralDataMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record LayerData(String layerName, Optional<String> paletteName, Boolean isVariant){
    public static final Codec<LayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(LayerData::layerName),
            Codec.STRING.optionalFieldOf("palette").forGetter(LayerData::paletteName),
            Codec.BOOL.fieldOf("is_variant").forGetter(LayerData::isVariant)
    ).apply(instance, LayerData::new));
}
