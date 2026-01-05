package com.honeyedlemons.verneuli.data.dataTypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Instrument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record DefaultGemVariant(
        Optional<ResourceLocation> entity,
        Optional<List<PaletteData>> palettes,
        Optional<Map<String, List<String>>> variants,
        Optional<List<LayerData>> layers,
        Optional<Holder<SoundEvent>> talkSound) {

    public static final Codec<DefaultGemVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("entity").forGetter(DefaultGemVariant::entity),
            Codec.list(PaletteData.CODEC).optionalFieldOf("palettes").forGetter(DefaultGemVariant::palettes),
            Codec.unboundedMap(Codec.STRING, Codec.list(Codec.STRING)).optionalFieldOf("variants").forGetter(DefaultGemVariant::variants),
            Codec.list(LayerData.CODEC).optionalFieldOf("layers").forGetter(DefaultGemVariant::layers),
            SoundEvent.CODEC.optionalFieldOf("talk_sound").forGetter(DefaultGemVariant::talkSound)
    ).apply(instance, DefaultGemVariant::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DefaultGemVariant> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC),
            DefaultGemVariant::entity,
            ByteBufCodecs.optional(PaletteData.STREAM_CODEC.apply(ByteBufCodecs.list())),
            DefaultGemVariant::palettes,
            ByteBufCodecs.optional(ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()))),
            DefaultGemVariant::variants,
            ByteBufCodecs.optional(LayerData.STREAM_CODEC.apply(ByteBufCodecs.list())),
            DefaultGemVariant::layers,
            ByteBufCodecs.optional(SoundEvent.STREAM_CODEC),
            DefaultGemVariant::talkSound,
            DefaultGemVariant::new
    );

    public static final Codec<Holder<DefaultGemVariant>> CODEC = RegistryFixedCodec.create(VerneuilDataTypes.DEFAULT_GEM_VARIANT);
}

