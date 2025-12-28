package com.honeyedlemons.verneuli.shared.data.datatypes;

import com.honeyedlemons.verneuli.shared.data.datamaps.BlockMineralDataMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.*;

public record GemVariant(
        ResourceLocation entity, ItemStack gemItem, String type, String translation, List<String> palettes, Map<String,List<String>> variants, List<LayerData> layers, Optional<BlockMineralDataMap.MineralData> crux
) {
    public static final Codec<GemVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("entity").forGetter(GemVariant::entity),
            ItemStack.STRICT_CODEC.fieldOf("gem_item").forGetter(GemVariant::gemItem),
            Codec.STRING.fieldOf("type").forGetter(GemVariant::type),
            Codec.STRING.fieldOf("translation").forGetter(GemVariant::translation),
            Codec.list(Codec.STRING).fieldOf("palettes").forGetter(GemVariant::palettes),
            Codec.unboundedMap(Codec.STRING,Codec.list(Codec.STRING)).fieldOf("variants").forGetter(GemVariant::variants),
            Codec.list(LayerData.CODEC).fieldOf("layers").forGetter(GemVariant::layers),
            BlockMineralDataMap.MineralData.CODEC.optionalFieldOf("crux").forGetter(GemVariant::crux)
    ).apply(instance, GemVariant::new));

    public static StreamCodec<ByteBuf, GemVariant> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static final EntityDataSerializer<GemVariant> GEM_VARIANT = EntityDataSerializer.forValueType(STREAM_CODEC);

    public GemVariant() {
        this(null,null, null, null, null, null, null, Optional.empty());
    }

    public EntityType<?> getEntity(RegistryAccess registryAccess)
    {
        var registry = registryAccess.lookupOrThrow(Registries.ENTITY_TYPE);
        return registry.getValue(this.entity());
    }
}

