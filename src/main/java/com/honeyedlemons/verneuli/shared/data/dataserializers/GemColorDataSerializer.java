package com.honeyedlemons.verneuli.shared.data.dataserializers;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.HashMap;
import java.util.Map;

public class GemColorDataSerializer {


    public static final Codec<Map<String,Integer>> CODEC = Codec.unboundedMap(
            Codec.STRING, Codec.INT);


    public static StreamCodec<ByteBuf, Map<String, Integer>> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    ByteBufCodecs.INT,
                    256
            );

    public static final EntityDataSerializer<Map<String, Integer>> GEM_COLOR = EntityDataSerializer.forValueType(STREAM_CODEC);

}
