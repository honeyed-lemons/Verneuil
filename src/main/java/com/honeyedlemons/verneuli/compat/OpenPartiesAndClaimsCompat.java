package com.honeyedlemons.verneuli.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import xaero.pac.common.server.api.OpenPACServerAPI;

public class OpenPartiesAndClaimsCompat {

	public static boolean canBreakBlock(ServerLevel level, LivingEntity livingEntity, BlockPos blockPos)
	{
		var minecraftServer = level.getServer();
		var serverApi = OpenPACServerAPI.get(minecraftServer);
		return serverApi.getChunkProtection().onEntityPlaceBlock(livingEntity,level,blockPos);
	}
}
