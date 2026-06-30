package com.honeyedlemons.verneuli.sounds;

import com.honeyedlemons.verneuli.Verneuil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VerneuilSounds {
	// Assuming that your mod id is examplemod
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
			DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Verneuil.MODID);

	// All vanilla sounds use variable range events.
	public static final Holder<SoundEvent> INJECTOR_FIRE = SOUND_EVENTS.register(
			"injector_fire",
			// Takes in the registry name
			SoundEvent::createVariableRangeEvent
	);
}
