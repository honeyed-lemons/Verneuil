package com.honeyedlemons.verneuli.commands;

import com.google.common.collect.ImmutableList;
import com.honeyedlemons.verneuli.entities.gems.AbstractGem;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public class RandomizeGemAppearanceCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("randomizegem").requires(Commands.hasPermission(2)).executes(p_137817_ -> VerneuilRandomizeGem(p_137817_.getSource(), ImmutableList.of(p_137817_.getSource().getEntityOrException()))).then(Commands.argument("targets", EntityArgument.entities()).executes(p_137810_ -> VerneuilRandomizeGem(p_137810_.getSource(), EntityArgument.getEntities(p_137810_, "targets")))));
	}

	private static int VerneuilRandomizeGem(CommandSourceStack source, Collection<? extends Entity> target) {
		int count = 0;
		ServerLevel server = source.getServer().overworld();
		for (Entity entity : target) {
			if (entity instanceof AbstractGem gem) {
				gem.generatePaletteColors(server);
				gem.generateLayerVariants(server);
				gem.adaptUniformColors();
				count++;
			}
		}
		switch (count) {
			case 0 -> {
				source.sendFailure(Component.translatable("vernueil.commands.randomize.failure"));
				return count;
			}
			case 1 ->
					source.sendSuccess(() -> Component.translatable("vernueil.commands.randomize.success.single", target.iterator().next().getDisplayName()), true);
			default ->
					source.sendSuccess(() -> Component.translatable("vernueil.commands.randomize.success.multiple", target.size()), true);
		}
		return count;
	}
}
