package com.honeyedlemons.verneuli.shared.data.datatypes;

import com.honeyedlemons.verneuli.Verneuil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Verneuil.MODID)
public class VerneuilDataTypeRegistry {
    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                VerneuilDataTypes.GEM_VARIANT,
                GemVariant.CODEC
        );
    }
}
