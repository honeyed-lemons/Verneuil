package com.honeyedlemons.verneuli.entities.items;

import com.honeyedlemons.verneuli.config.VerneuilConfigServer;
import com.honeyedlemons.verneuli.entities.VerneuilEntities;
import com.honeyedlemons.verneuli.items.GemItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class GemItemEntity extends ItemEntity {
	private int reformTicks;

	public GemItemEntity(EntityType<? extends ItemEntity> entityType, Level level) {
		super(entityType, level);
	}

	public GemItemEntity(Level level, double posX, double posY, double posZ, ItemStack itemStack) {
		this(level, posX, posY, posZ, itemStack, level.random.nextDouble() * 0.2 - 0.1, 0.2, level.random.nextDouble() * 0.2 - 0.1);
	}

	public GemItemEntity(Level level, double posX, double posY, double posZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ) {
		this(VerneuilEntities.GEM_ITEM_ENTITY.get(), level);
		this.setPos(posX, posY, posZ);
		this.setDeltaMovement(deltaX, deltaY, deltaZ);
		this.setItem(itemStack);
		this.lifespan = itemStack.getEntityLifespan(level);
	}
	protected void addAdditionalSaveData(@NotNull ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putInt("ReformTicks",reformTicks);
	}

	protected void readAdditionalSaveData(@NotNull ValueInput input) {
		super.readAdditionalSaveData(input);
		this.reformTicks = input.getIntOr("ReformTicks",0);
	}
	@Override
	public void tick() {
		super.tick();
		if (this.tickCount % 20 == 0 && !this.level().isClientSide()) {
			int reformSeconds = VerneuilConfigServer.CONFIG.reformTime.get();
			if(reformTicks >= reformSeconds)
			{
				if (this.getItem().getItem() instanceof GemItem gemItem)
				{
					gemItem.summonGem(this.getItem(),this.level(), BlockPos.containing(this.position()),null);
				}
			}
			else {
				reformTicks++;
			}
		}
	}
}