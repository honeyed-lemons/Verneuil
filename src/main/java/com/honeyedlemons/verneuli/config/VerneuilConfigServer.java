package com.honeyedlemons.verneuli.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class VerneuilConfigServer {
	public static final VerneuilConfigServer CONFIG;
	public static final ModConfigSpec CONFIG_SPEC;

	public final ModConfigSpec.IntValue reformTime;

	public final ModConfigSpec.IntValue incubationChance;

	public final ModConfigSpec.BooleanValue canPickUp;

	private VerneuilConfigServer(ModConfigSpec.Builder builder) {
		reformTime = builder
				.comment("How long a gem takes to reform in seconds.")
				.translation("verneuil.configuration.reformTime")
				.defineInRange("reformTime", 60, 1, Integer.MAX_VALUE);
		incubationChance = builder
				.comment("An integer is rolled between 0 and this number every time the geode randomly ticks, if it returns 0, then the geode will grow.")
				.translation("verneuil.configuration.incubationTime")
				.defineInRange("incubationTime", 9, 1, Integer.MAX_VALUE);
		canPickUp = builder
				.comment("whether or not a gem will path to and pick up equippables")
				.translation("verneuil.configuration.canPickUp")
				.define("canPickUp", true);
	}

	static {
		Pair<VerneuilConfigServer, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(VerneuilConfigServer::new);

		//Store the resulting values
		CONFIG = pair.getLeft();
		CONFIG_SPEC = pair.getRight();
	}

}
