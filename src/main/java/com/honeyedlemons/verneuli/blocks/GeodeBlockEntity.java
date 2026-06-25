package com.honeyedlemons.verneuli.blocks;

import com.honeyedlemons.verneuli.compat.OpenPartiesAndClaimsCompat;
import com.honeyedlemons.verneuli.config.VerneuilConfigServer;
import com.honeyedlemons.verneuli.data.dataTypes.CruxData;
import com.honeyedlemons.verneuli.data.dataTypes.GemVariant;
import com.honeyedlemons.verneuli.data.dataTypes.MineralData;
import com.honeyedlemons.verneuli.entities.gems.AbstractGem;
import com.honeyedlemons.verneuli.util.DrainUtil;
import com.honeyedlemons.verneuli.util.RegistryUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GeodeBlockEntity extends BlockEntity {

	public GeodeBlockEntity(BlockPos pos, BlockState blockState) {
		super(VerneuilBlocks.GEODE_ENTITY.get(), pos, blockState);
	}

	private final MineralData geologicalData = new MineralData(new HashMap<>());
	private @Nullable EntityReference<LivingEntity> ownedBy;

	public static class GeodeBlock extends Block implements EntityBlock {

		public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

		public GeodeBlock(Properties properties) {
			super(properties);
			this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
		}

		@Override
		protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(AGE);
		}

		@Override
		public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
			return new GeodeBlockEntity(pos, state);
		}

		@Override
		public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
			super.setPlacedBy(level, pos, state, by, itemStack);

			if (level.getBlockEntity(pos) instanceof GeodeBlockEntity geode)
				geode.ownedBy = EntityReference.of(by);
		}

		@Override
		protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
			if (!(level.getBlockEntity(pos) instanceof GeodeBlockEntity geode))
				return InteractionResult.FAIL;

			if (!(level instanceof ServerLevel))
				return InteractionResult.FAIL;

			var gemVariant = geode.getGemVariant();

			if (gemVariant != null) {
				player.sendOverlayMessage(Component.literal(gemVariant.getFirst().gemItem().create().getDisplayName().getString() + " " + gemVariant.getSecond().toString()));
				return InteractionResult.SUCCESS;
			}

			return super.useWithoutItem(state, level, pos, player, hitResult);
		}

		protected boolean isRandomlyTicking(BlockState state) {
			return this.getAge(state) <= getMaxAge();
		}

		protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
			int age = this.getAge(state);
			int incubationChance = VerneuilConfigServer.CONFIG.incubationChance.get();

			if (!level.isAreaLoaded(pos, 1))
				return;

			// Drain a block in an area
			if ((level.getBlockEntity(pos) instanceof GeodeBlockEntity geode)) {
				for (int i = 0; i < ((age + 1) * 8); i++) {
					DrainUtil.drainInArea(geode, level, pos, getAge(state));
				}
			}

			if (random.nextInt((incubationChance)) == 0) {
				if (age < this.getMaxAge()) {
					level.setBlockAndUpdate(pos, this.getStateForAge(age + 1));
				}
				else {
					if ((level.getBlockEntity(pos) instanceof GeodeBlockEntity geode))
						geode.emerge();
				}
			}
		}

		public BlockState getStateForAge(int age) {
			return this.defaultBlockState().setValue(this.getAgeProperty(), age);
		}

		protected int getMaxAge() {
			return 3;
		}

		protected IntegerProperty getAgeProperty() {
			return AGE;
		}

		public int getAge(BlockState state) {
			return state.getValue(this.getAgeProperty());
		}
	}

	public Pair<GemVariant, Double> getGemVariant() {
		if (this.getLevel() == null)
			return null;

		CruxData cruxData = DrainUtil.getCruxData(this.getLevel(), this);

		var gemVariantEntryset = RegistryUtil.getGemVariantRegistry(this.getLevel()).entrySet();

		// TEMP
		double minDifference = Double.MAX_VALUE;

		GemVariant closestVariant = null;

		double finalDifference = 0;

		for (Map.Entry<ResourceKey<GemVariant>, GemVariant> entry : gemVariantEntryset) {
			if (entry.getValue().crux().isEmpty())
				continue;

			double difference = CruxData.getCruxSimilarity(entry.getValue().crux().get(), cruxData);

			if (!(difference <= minDifference) || (finalDifference != 0 && !(difference <= finalDifference)))
				continue;

			finalDifference = difference;
			closestVariant = entry.getValue();
		}

		return new Pair<>(closestVariant, finalDifference);
	}

	public MineralData getMineralData() {
		return this.geologicalData;
	}

	public EntityReference<LivingEntity> getOwnedBy() {
		return this.ownedBy;
	}

	private Entity spawnGem(EntityType<LivingEntity> entityType, Level level, BlockPos pos, EntitySpawnReason spawnReason) {
		var serverLevel = (ServerLevel) level;
		return entityType.spawn(serverLevel, pos, spawnReason);
	}

	private void emerge() {
		if (getLevel() == null)
			return;

		var gemVariant = getGemVariant();

		var entityType = gemVariant.getFirst().entity();
		if (entityType.isEmpty())
			return;

		var gemType = RegistryUtil.getEntityTypeRegistry(getLevel()).get(entityType.get());
		if (gemType.isEmpty())
			return;

		var entity = gemType.get().value().spawn((ServerLevel) getLevel(), getBlockPos(), EntitySpawnReason.NATURAL);

		if (!(entity instanceof AbstractGem gem))
			return;

		gem.setGemVariant(gemVariant.getFirst(), true, true);

		createExitHole(gem, getLevel());
	}

	private void createExitHole(AbstractGem gem, Level level) {
		int gemHeight = (int) Math.ceil(gem.getBbHeight());

		var claimsLoaded = ModList.get().isLoaded("openpartiesandclaims");
		var owner = EntityReference.getLivingEntity(this.getOwnedBy(), level);

		var closestAir = DrainUtil.findClosestAir(level, getBlockPos());
		var blockToExitTo = closestAir != null ? closestAir : getBlockPos();

		var exitHoleBlocks = BlockPos.betweenClosed(getBlockPos().above(gemHeight - 1), blockToExitTo);
		var particleBlocks = BlockPos.betweenClosed(blockToExitTo.above(gemHeight - 1), blockToExitTo);

		for (BlockPos blockPos : exitHoleBlocks) {
			if (claimsLoaded && OpenPartiesAndClaimsCompat.canBreakBlock((ServerLevel) level, owner, blockPos))
				continue;

			if (level.getBlockState(blockPos).is(BlockTags.FEATURES_CANNOT_REPLACE))
				continue;

			level.destroyBlock(blockPos, false);
		}

		var direction = blockToExitTo.getCenter().subtract(getBlockPos().getCenter()).normalize();

		createEmergeParticles(particleBlocks,direction, Minecraft.getInstance().level, level.getRandom()); // This seems wrong but. uhm. it works?

		// And finally, destroy the geode
		level.destroyBlock(getBlockPos(), false);
	}

	private void createEmergeParticles(Iterable<BlockPos> blockPositions, Vec3 direction, Level level, RandomSource random)
	{
		var particleType = ParticleTypes.END_ROD;

		var particleCount = 8;

		for(BlockPos blockPos : blockPositions) {
			var center = blockPos.getCenter();
			for (int i = 0; i < particleCount; i++) {
				double x = center.x() + (random.nextDouble() - 0.5);
				double y = center.y() + (random.nextDouble() - 0.5);
				double z = center.z() + (random.nextDouble() - 0.5);
				level.addParticle(particleType, x, y, z, direction.x * 0.08, 0.1, direction.z * 0.08);
			}
		}
	}

	@Override
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		input.read("geo_data", MineralData.CODEC).ifPresent(MineralData::new);
		this.ownedBy = EntityReference.read(input, "owned_by");
	}

	@Override
	public void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.store("geo_data", MineralData.CODEC, this.geologicalData);
		EntityReference.store(this.ownedBy, output, "owned_by");
	}
}
