package com.honeyedlemons.verneuli.shared.data.dataTypes;

import com.honeyedlemons.verneuli.Verneuil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Verneuil.MODID)
public class VerneuilDataTypeRegistry {
    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                VerneuilDataTypes.DEFAULT_GEM_VARIANT,
                DefaultGemVariant.DIRECT_CODEC,
                DefaultGemVariant.DIRECT_CODEC
        );
        event.dataPackRegistry(
                VerneuilDataTypes.GEM_VARIANT,
                GemVariant.DIRECT_CODEC,
                GemVariant.DIRECT_CODEC
        );

    }
}
