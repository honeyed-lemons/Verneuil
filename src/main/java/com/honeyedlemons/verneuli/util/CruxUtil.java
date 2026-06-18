package com.honeyedlemons.verneuli.util;

import com.honeyedlemons.verneuli.blocks.DrainedColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static com.honeyedlemons.verneuli.blocks.DrainedBlock.DRAINED_COLOR;
import static com.honeyedlemons.verneuli.blocks.DrainedBlock.INDEX;

public class CruxUtil {

	public static float getTemperature(Level level, BlockPos pos)
	{
		return level.getBiome(pos).value().getBaseTemperature();
	}

	public static BlockState getDrainedBlockstate(Level level, BlockPos pos, BlockState defaultState)
	{
		// If it isn't a drained block don't try to get like. the shit below. okay?
		if (!defaultState.hasProperty(DRAINED_COLOR) || !defaultState.hasProperty(INDEX))
			return defaultState;

		var state = defaultState;
		var temperature = getTemperature(level, pos);

		if (temperature <= 0.25)
			state = state.setValue(DRAINED_COLOR, DrainedColor.Blue);
		else if (temperature >= 0.95)
			state = state.setValue(DRAINED_COLOR,DrainedColor.Red);
		else
			state = state.setValue(DRAINED_COLOR,DrainedColor.Purple);

		if (pos.getY() <= 0)
			state = state.setValue(DRAINED_COLOR,DrainedColor.Black);

		state = state.setValue(INDEX,Math.abs(pos.getY() % 3));

		return state;
	}
}
