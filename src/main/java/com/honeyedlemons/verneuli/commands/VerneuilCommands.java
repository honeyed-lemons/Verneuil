package com.honeyedlemons.verneuli.commands;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class VerneuilCommands {

	@SubscribeEvent
	public static void onCommandsRegister(RegisterCommandsEvent event) {
		RandomizeGemAppearanceCommand.register(event.getDispatcher());
	}
}
