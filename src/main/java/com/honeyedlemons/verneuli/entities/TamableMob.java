package com.honeyedlemons.verneuli.entities;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TamableMob extends PathfinderMob implements OwnableEntity {
	protected static final EntityDataAccessor<Byte> MOVEMENT_TYPE = SynchedEntityData.defineId(TamableMob.class, EntityDataSerializers.BYTE);
	protected static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(TamableMob.class, EntityDataSerializers.BOOLEAN);

	@Nullable
	private EntityReference<LivingEntity> owner;

	protected TamableMob(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
		super.defineSynchedData(builder);
		builder.define(TAMED, false);
		builder.define(MOVEMENT_TYPE, (byte) 0);
	}

	@Override
	protected void addAdditionalSaveData(@NotNull ValueOutput output) {
		super.addAdditionalSaveData(output);
		EntityReference.store(this.owner, output, "Owner");
		output.putBoolean("Tame", this.isTame());
		output.putByte("MovementType", this.getMovementType());
	}

	@Override
	protected void readAdditionalSaveData(@NotNull ValueInput input) {
		super.readAdditionalSaveData(input);
		this.owner = EntityReference.readWithOldOwnerConversion(input, "Owner", this.level());
		this.setTame(input.getBooleanOr("Tame", false),false);
		this.setMovementType(input.getByteOr("MovementType",(byte) 0));
	}

	@javax.annotation.Nullable
	@Override
	public EntityReference<LivingEntity> getOwnerReference() {
		return this.owner;
	}

	public void setOwner(@javax.annotation.Nullable LivingEntity owner) {
		this.owner = EntityReference.of(owner);
	}


	public void tame(Player player, @Nullable Component message)
	{
		this.setTame(true, true);
		this.setOwner(player);

		if (message != null)
			player.displayClientMessage(message, false);
	}

	public boolean isTame() {
		return (this.entityData.get(TAMED));
	}

	public void setTame(boolean tame, boolean applyTamingSideEffects) {
		this.entityData.set(TAMED, tame);

		if (applyTamingSideEffects) {
			this.applyTamingSideEffects();
		}
	}

	protected void applyTamingSideEffects() {
	}

	public boolean isOwnedBy(LivingEntity entity) {
		return entity == this.getOwner();
	}

	public boolean doesShareOwner(LivingEntity entity)
	{
		if (entity instanceof OwnableEntity ownable)
		{
			return ownable.getOwner() == this.getOwner();
		}
		return false;
	}

	@Override
	public void die(@NotNull DamageSource cause) {
		Component deathMessage = this.getCombatTracker().getDeathMessage();
		super.die(cause);

		if (this.dead)
			if (this.level() instanceof ServerLevel serverlevel
					&& serverlevel.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)
					&& this.getOwner() instanceof ServerPlayer serverplayer) {
				serverplayer.sendSystemMessage(deathMessage);
			}
	}

	public boolean isMovementType(int state)
	{
		return this.entityData.get(MOVEMENT_TYPE).equals((byte) state);
	}

	public byte getMovementType()
	{
		return this.entityData.get(MOVEMENT_TYPE);
	}

	public void setMovementType(int state)
	{
		this.entityData.set(MOVEMENT_TYPE, (byte) state);
	}

	public void cycleMovementType()
	{
		var movementType = this.getMovementType();

		if (movementType + 1 >= 3)
			movementType = 0;
		else
			movementType += 1;

		this.setMovementType(movementType);
	}
	@Override
	public boolean canHaveALeashAttachedTo(@NotNull Entity entity) {
		if (entity instanceof LivingEntity livingEntity && !this.isOwnedBy(livingEntity))
		{
			return false;
		}
		return super.canHaveALeashAttachedTo(entity);
	}

	public boolean isFollowing(PathfinderMob pathfinderMob) {
		if (pathfinderMob instanceof TamableMob)
			return this.isMovementType(2);
		return false;
	}

	public boolean isStaying(PathfinderMob pathfinderMob) {
		if (pathfinderMob instanceof TamableMob)
			return this.isMovementType(1);
		return false;
	}

	public boolean isWandering(PathfinderMob pathfinderMob) {
		if (pathfinderMob instanceof TamableMob)
			return this.isMovementType(0);
		return false;
	}
}
