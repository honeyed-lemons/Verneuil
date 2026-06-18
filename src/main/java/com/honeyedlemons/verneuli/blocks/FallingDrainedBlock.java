package com.honeyedlemons.verneuli.blocks;

import com.honeyedlemons.verneuli.util.CruxUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.Nullable;

public class FallingDrainedBlock extends FallingBlock {

	public static final EnumProperty<DrainedColor> DRAINED_COLOR = EnumProperty.create("drained_color", DrainedColor.class);

	public FallingDrainedBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(DRAINED_COLOR, DrainedColor.Purple));
	}
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DRAINED_COLOR);
	}
	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		return CruxUtil.getDrainedBlockstate(context.getLevel(),context.getClickedPos(),this.defaultBlockState());
	}

	public static final MapCodec<FallingDrainedBlock> CODEC = simpleCodec(FallingDrainedBlock::new);

	@Override
	protected MapCodec<? extends FallingBlock> codec() {
		return CODEC;
	}

	@Override
	public int getDustColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
		return blockState.getMapColor(blockGetter, blockPos).col;
	}
}

