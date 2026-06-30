package com.honeyedlemons.verneuli.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class TripleBlock extends Block {
	public static final EnumProperty<TripleBlockSection> SECTION = EnumProperty.create("third", TripleBlockSection.class);
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

	public TripleBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(SECTION, TripleBlockSection.LOWER).setValue(FACING,Direction.NORTH));
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random)
	{
		if (getSections(state,pos).containsValue(neighbourPos))
		{
			return neighbourState.is(this) && neighbourState.getValue(SECTION) != state.getValue(SECTION)
					? state : Blocks.AIR.defaultBlockState();
		}
		else
			return super.updateShape(state,level,ticks,pos,directionToNeighbour,neighbourPos,neighbourState,random);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide() && player.preventsBlockDrops()){
			getSections(state,pos).forEach(((section, otherPos) ->
			{
				var otherState = level.getBlockState(otherPos);

				if (section == TripleBlockSection.MIDDLE){
					level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
					level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
				}
			}));
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	public Map<TripleBlockSection,BlockPos> getSections(BlockState state, BlockPos pos)
	{
		var map = new HashMap<TripleBlockSection,BlockPos>();
		var section = state.getValue(SECTION);
		map.put(section,pos);

		switch (section)
		{
			case LOWER -> {
				map.put(TripleBlockSection.MIDDLE,(pos.above()));
				map.put(TripleBlockSection.UPPER,(pos.above(2)));
			}
			case MIDDLE -> {
				map.put(TripleBlockSection.LOWER,(pos.below()));
				map.put(TripleBlockSection.UPPER,(pos.above()));
			}
			case UPPER -> {
				map.put(TripleBlockSection.MIDDLE,(pos.below()));
				map.put(TripleBlockSection.LOWER,(pos.below(2)));
			}
		}

		return map;
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();

		if (pos.getY() >= level.getMaxY() - 2) {
			return null;
		}

		if (!level.getBlockState(pos.above()).canBeReplaced(context)
				|| !level.getBlockState(pos.above(2)).canBeReplaced(context)) {
			return null;
		}

		return defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(SECTION, TripleBlockSection.LOWER);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
		super.setPlacedBy(level, pos, state, by, itemStack);
		level.setBlock(pos.above(), state.setValue(SECTION, TripleBlockSection.MIDDLE), 3);
		level.setBlock(pos.above(2), state.setValue(SECTION, TripleBlockSection.UPPER), 3);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SECTION).add(FACING);
	}
}
