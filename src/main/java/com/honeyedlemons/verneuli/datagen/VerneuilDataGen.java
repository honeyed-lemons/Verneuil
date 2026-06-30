package com.honeyedlemons.verneuli.datagen;

import com.honeyedlemons.verneuli.Verneuil;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = Verneuil.MODID)
public class VerneuilDataGen {
	@SubscribeEvent // on the mod event bus
	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider(VerneuilLanguageProvider::new);
		event.createProvider(VerneuilBlockTagsProvider::new);
		event.createProvider(VerneuilModelProvider::new);
		event.createProvider((output, lookupProvider) ->
				new LootTableProvider(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(
						VerneuilBlockLootTableSubProvider::new,
						LootContextParamSets.BLOCK)),
						lookupProvider)
		);
	}
}
