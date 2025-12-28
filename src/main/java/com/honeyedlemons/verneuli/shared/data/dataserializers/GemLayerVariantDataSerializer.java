package com.honeyedlemons.verneuli.shared.data.dataserializers;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.HashMap;
import java.util.Map;

public class GemLayerVariantDataSerializer {
    public static final Codec<Map<String,String>> CODEC = Codec.unboundedMap(
            Codec.STRING, Codec.STRING);


    public static StreamCodec<ByteBuf, Map<String, String>> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    ByteBufCodecs.STRING_UTF8,
                    256
            );

    public static final EntityDataSerializer<Map<String, String>> GEM_LAYER_VARIANTS = EntityDataSerializer.forValueType(STREAM_CODEC);
}
