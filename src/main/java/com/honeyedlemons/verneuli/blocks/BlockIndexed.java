package com.honeyedlemons.verneuli.blocks;

import com.honeyedlemons.verneuli.util.DrainUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jspecify.annotations.Nullable;

public class BlockIndexed extends Block {
	public static final IntegerProperty INDEX = IntegerProperty.create("index", 0, 2);

	public BlockIndexed(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(INDEX,0));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
		super.setPlacedBy(level, pos, state, by, itemStack);
		level.setBlock(pos, state.setValue(INDEX, DrainUtil.getDrainedIndex(level,pos)),UPDATE_CLIENTS);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(INDEX);
	}
}

