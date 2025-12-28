package com.honeyedlemons.verneuli.shared.data.datacomponents;

import com.honeyedlemons.verneuli.shared.data.saveddata.GemSavedData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record GemDataRecord(UUID uuid) {
    public static final Codec<GemDataRecord> BASIC_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.fieldOf("gem_uuid").forGetter(GemDataRecord::uuid)
            ).apply(instance, GemDataRecord::new)
    );

    public static final StreamCodec<ByteBuf, GemDataRecord> UNIT_STREAM_CODEC = ByteBufCodecs.fromCodec(BASIC_CODEC);

    public GemDataRecord with(UUID uuid)
    {
        return new GemDataRecord(uuid);
    }
}
