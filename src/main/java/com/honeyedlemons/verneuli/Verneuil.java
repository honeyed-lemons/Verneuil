package com.honeyedlemons.verneuli;

import com.honeyedlemons.verneuli.shared.blocks.VerneuilBlocks;
import com.honeyedlemons.verneuli.shared.data.datacomponents.VerneuilDataComponents;
import com.honeyedlemons.verneuli.shared.data.datamaps.*;
import com.honeyedlemons.verneuli.shared.data.dataserializers.VerneuilDataSerializers;
import com.honeyedlemons.verneuli.shared.entities.VerneuilEntities;
import com.honeyedlemons.verneuli.shared.items.VerneuilItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Verneuil.MODID)
public class Verneuil {
    public static final String MODID = "verneuil";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public Verneuil(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        VerneuilBlocks.BLOCK_ENTITY_TYPES.register(modEventBus);
        VerneuilBlocks.BLOCKS.register(modEventBus);
        VerneuilItems.ITEMS.register(modEventBus);
        VerneuilDataSerializers.DATA_SERIALIZERS.register(modEventBus);
        VerneuilDataComponents.DATA_COMPONENTS.register(modEventBus);
        VerneuilEntities.ENTITY_TYPES.register(modEventBus);

        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.register(BlockMineralDataMap.class);
        modEventBus.register(EntityGemVariantsDataMap.class);

        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
