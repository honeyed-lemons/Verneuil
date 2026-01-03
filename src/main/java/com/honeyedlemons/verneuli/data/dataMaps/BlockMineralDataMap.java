package com.honeyedlemons.verneuli.data.dataMaps;

import com.honeyedlemons.verneuli.Verneuil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.Map;

public class BlockMineralDataMap {
    public record MineralData(Map<String,Float> data) {
        public static final Codec<MineralData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("mineral_data").forGetter(MineralData::data)
        ).apply(instance, MineralData::new));
    }
    public static final DataMapType<Block, MineralData> MINERAL_DATA = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(Verneuil.MODID, "mineral_data"),
            Registries.BLOCK,
            MineralData.CODEC
    ).build();

    @SubscribeEvent
    public static void register(RegisterDataMapTypesEvent event) {
        event.register(MINERAL_DATA);
    }
}
