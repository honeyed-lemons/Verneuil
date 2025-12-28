package com.honeyedlemons.verneuli.shared.data.datacomponents;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.honeyedlemons.verneuli.Verneuil.MODID;

public class VerneuilDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE,MODID);

    // No data will be synced across the network
    public static final Supplier<DataComponentType<GemDataRecord>> GEM_DATA = DATA_COMPONENTS.registerComponentType(
            "gem_data",
            builder -> builder
                    .persistent(GemDataRecord.BASIC_CODEC)
                    // Note we use a unit stream codec here
                    .networkSynchronized(GemDataRecord.UNIT_STREAM_CODEC)
    );
}
