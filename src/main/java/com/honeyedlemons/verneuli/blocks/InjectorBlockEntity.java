package com.honeyedlemons.verneuli.blocks;

import com.honeyedlemons.verneuli.config.VerneuilConfigServer;
import com.honeyedlemons.verneuli.items.VerneuilItems;
import com.honeyedlemons.verneuli.sounds.VerneuilSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class InjectorBlockEntity extends BlockEntity implements ContainerSingleItem.BlockContainerSingleItem {

	private @Nullable EntityReference<LivingEntity> ownedBy;
	
	private ItemStack item = ItemStack.EMPTY;

	public InjectorBlockEntity(BlockPos pos, BlockState blockState) {
		super(VerneuilBlocks.INJECTOR_ENTITY.get(), pos, blockState);
	}

	public static class InjectorBlock extends TripleBlock implements EntityBlock {

		public static final BooleanProperty FULL = BooleanProperty.create("full");

		private static final Map<Direction, VoxelShape> SHAPES_UPPER = Shapes.rotateHorizontal(
				Shapes.or(
						Block.box(1, 1, 1, 15, 16, 15),
						Block.box(2, 0, 2, 14, 1, 14)
				)
		);

		private static final Map<Direction, VoxelShape> SHAPES_MIDDLE = Shapes.rotateHorizontal(
				Shapes.or(
						Block.box(1, 0, 1, 15, 16, 15),
						Block.box(3, 0, 14, 13, 15, 16)
				)
		);

		private static final Map<Direction, VoxelShape> SHAPES_MIDDLE_SUPPORT_SHAPE = Shapes.rotateHorizontal(Shapes.or(Block.box(0, 0, 15, 16, 16, 16)));

		private static final Map<Direction, VoxelShape> SHAPES_LOWER = Shapes.rotateHorizontal(
				Shapes.or(
						Block.box(0, 13, 0, 16, 16, 16),
						Block.box(1, 8, 1, 15, 13, 15),
						Block.box(6, 4, 6, 10, 8, 10),
						Block.box(7, 1, 7, 9, 4, 9)
				)
		);

		public InjectorBlock(Properties properties) {
			super(properties);
			this.registerDefaultState(this.stateDefinition.any()
					.setValue(SECTION, TripleBlockSection.LOWER)
					.setValue(FACING, Direction.NORTH)
					.setValue(FULL, false));
		}
		@Override
		protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
			if (level.isClientSide()) {
				return InteractionResult.SUCCESS_SERVER;
			}

			if (level.getBlockEntity(pos) instanceof InjectorBlockEntity injector) {
				injector.popOutTheItem();
				injector.removeOwnedBy();
				return InteractionResult.SUCCESS;
			}

			return InteractionResult.PASS;
		}

		@Override
		protected InteractionResult useItemOn(
				ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
		) {
			if (state.getValue(FULL) || level.isClientSide()) {
				return InteractionResult.TRY_WITH_EMPTY_HAND;
			} else {
				ItemStack toInsert = player.getItemInHand(hand);
				if (toInsert.getItem() == VerneuilItems.GEM_SEED.asItem() && level.getBlockEntity(pos) instanceof InjectorBlockEntity injector)
				{
					injector.setTheItem(toInsert.split(1));
					injector.setOwnedBy(player);
					return InteractionResult.CONSUME;
				}
				return InteractionResult.TRY_WITH_EMPTY_HAND;
			}
		}

		@Override
		protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston)
		{
			super.neighborChanged(state,level,pos,block,orientation,movedByPiston);

			if (block == this)
				return;

			var middleSignal = level.getBestNeighborSignal(pos);

			if (!state.getValue(FULL) || state.getValue(SECTION) != TripleBlockSection.MIDDLE || middleSignal <= 0)
				return;

			if(inject(level, pos.below(), getInjectionDepth(middleSignal)))
			{
				var clientLevel = Minecraft.getInstance().level;
				if (clientLevel != null)
					clientLevel.playLocalSound(pos, VerneuilSounds.INJECTOR_FIRE.value(), SoundSource.BLOCKS, 4.0f, 1.0f, false);
			}
		}


		@Override
		protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
			var section = state.getValue(SECTION);
			Direction facing = state.getValue(FACING);

			return switch (section) {
				case LOWER -> SHAPES_LOWER.get(facing);
				case MIDDLE -> SHAPES_MIDDLE.get(facing);
				case UPPER -> SHAPES_UPPER.get(facing);
			};
		}

		@Override
		protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
			var facing = state.getValue(FACING);
			return switch (state.getValue(SECTION)){
				case LOWER, UPPER -> Shapes.empty();
				case MIDDLE -> SHAPES_MIDDLE_SUPPORT_SHAPE.get(facing);
			};
		}

		private int getInjectionDepth(int redstoneSignal)
		{
			int defaultInjectionDepth = VerneuilConfigServer.CONFIG.injectionDepth.get();

			return (int) (((float) redstoneSignal / 15) * defaultInjectionDepth);
		}

		private boolean inject(Level level, BlockPos pos, int depth) {
			if (level.isClientSide() || !(level.getBlockEntity(pos.above()) instanceof InjectorBlockEntity injector))
				return false;
			var depthAttempts = 8;
			BlockPos geodePos = null;

			for (int i = 1; i < depthAttempts; i ++)
			{
				var testDepth = pos.below(depth * i);
				if (!level.getBlockState(testDepth).is(VerneuilBlocks.GEODE.get())) {
					geodePos = testDepth;
					break;
				}
			}

			if (geodePos == null)
				return false;

			level.setBlockAndUpdate(geodePos,VerneuilBlocks.GEODE.get().defaultBlockState());

			if (injector.getOwnedBy() != null && level.getBlockEntity(geodePos) instanceof  GeodeBlockEntity geodeEntity)
				geodeEntity.setOwnedBy(injector.getOwnedBy());

			injector.removeItem(0,1);
			return true;
		}

		@Override
		public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
			if (state.getValue(SECTION) == TripleBlockSection.MIDDLE)
				return new InjectorBlockEntity(pos, state);

			else return null;
		}

		@Override
		protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(SECTION).add(FACING).add(FULL);
		}
	}


	public void setOwnedBy(LivingEntity entity)
	{
		this.ownedBy = EntityReference.of(entity);
	}

	public void removeOwnedBy()
	{
		this.ownedBy = null;
	}

	public @Nullable EntityReference<LivingEntity> getOwnedBy()
	{
		return this.ownedBy;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack itemStack) {
		return itemStack.getItem() == VerneuilItems.GEM_SEED.asItem() && !getFullness();
	}

	@Override
	public ItemStack getTheItem() {
		return this.item;
	}

	@Override
	public ItemStack splitTheItem(int count) {
		ItemStack retrievedItem = this.item;
		this.setTheItem(ItemStack.EMPTY);
		setFullness(false);
		return retrievedItem;
	}

	@Override
	public void setTheItem(ItemStack itemStack) {
		this.item = itemStack;
		setFullness(true);
	}

	private void setFullness(boolean fullness)
	{
		var level = this.getLevel();
		if (level == null || level.isClientSide())
			return;

		var blockState = level.getBlockState(this.getBlockPos());

		level.setBlockAndUpdate(this.getBlockPos(),blockState.setValue(InjectorBlock.FULL,fullness));
	}

	private boolean getFullness()
	{
		var level = this.getLevel();
		if (level == null || level.isClientSide())
			return false;

		return level.getBlockState(this.getBlockPos()).getValue(InjectorBlock.FULL);
	}

	public void popOutTheItem() {
		if (this.level != null && !this.level.isClientSide()) {
			BlockPos pos = this.getBlockPos();
			ItemStack itemBeforePoppingOut = this.getTheItem();
			if (!itemBeforePoppingOut.isEmpty()) {
				this.removeTheItem();
				Vec3 itemPos = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandomXZ(this.level.getRandom(), 0.7F);
				ItemStack itemStack = itemBeforePoppingOut.copy();
				ItemEntity entity = new ItemEntity(this.level, itemPos.x(), itemPos.y(), itemPos.z(), itemStack);
				entity.setDefaultPickUpDelay();
				this.level.addFreshEntity(entity);
				level.setBlockAndUpdate(pos,level.getBlockState(pos).setValue(InjectorBlock.FULL,false));
			}
		}
	}

	@Override
	public BlockEntity getContainerBlockEntity() {
		return this;
	}
	
	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.ownedBy = EntityReference.read(input, "OwnedBy");

		this.item = input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		EntityReference.store(this.ownedBy, output, "OwnedBy");
		if (!this.getTheItem().isEmpty()) {
			output.store("Item", ItemStack.CODEC, this.getTheItem());
		}
	}
}
