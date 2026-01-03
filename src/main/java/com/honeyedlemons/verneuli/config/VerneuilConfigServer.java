package com.honeyedlemons.verneuli.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class VerneuilConfigServer {
    public static final VerneuilConfigServer CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.IntValue reformTime;

    private VerneuilConfigServer(ModConfigSpec.Builder builder) {
        reformTime = builder
                .comment("How long a gem takes to reform.")
                .translation("verneuil.configuration.reformTime")
                .defineInRange("reformTime", 60, 1, Integer.MAX_VALUE);
    }

    static {
        Pair<VerneuilConfigServer, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(VerneuilConfigServer::new);

        //Store the resulting values
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

}
