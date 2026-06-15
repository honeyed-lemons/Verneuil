package com.honeyedlemons.verneuli.data.dataMaps;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.data.dataTypes.BlockDrainData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class BlockDrainDataMap {
	public static final DataMapType<Block, BlockDrainData> BLOCK_DRAIN_DATA = DataMapType
			.builder(ResourceLocation.fromNamespaceAndPath(Verneuil.MODID, "drain_data"), Registries.BLOCK, BlockDrainData.CODEC).build();

	@SubscribeEvent
	public static void register(RegisterDataMapTypesEvent event) {
		event.register(BLOCK_DRAIN_DATA);
	}
}
