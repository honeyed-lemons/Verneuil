package com.honeyedlemons.verneuli.blocks;

import com.honeyedlemons.verneuli.util.DrainUtil;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jspecify.annotations.Nullable;

public class DrainedBlock extends Block {

	public static final EnumProperty<DrainedColor> DRAINED_COLOR = EnumProperty.create("drained_color", DrainedColor.class);
	public static final IntegerProperty INDEX = IntegerProperty.create("index", 0, 2);

	public DrainedBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(DRAINED_COLOR, DrainedColor.Purple).setValue(INDEX,0));
	}
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DRAINED_COLOR,INDEX);
	}
	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		return DrainUtil.getDrainedBlockstate(context.getLevel(),context.getClickedPos(),this.defaultBlockState());
	}
}

