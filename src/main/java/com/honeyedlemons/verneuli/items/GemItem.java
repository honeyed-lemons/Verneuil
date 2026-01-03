package com.honeyedlemons.verneuli.items;

import com.honeyedlemons.verneuli.data.dataComponents.VerneuilDataComponents;
import com.honeyedlemons.verneuli.data.dataTypes.GemVariant;
import com.honeyedlemons.verneuli.data.dataTypes.VerneuilDataTypes;
import com.honeyedlemons.verneuli.data.savedData.GemSavedData;
import com.honeyedlemons.verneuli.entities.gems.AbstractGem;
import com.honeyedlemons.verneuli.entities.items.GemItemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GemItem extends Item implements IItemExtension {

    private final ResourceLocation gemVariantLocation;
    public final EntityType<?> entityType;

    public GemItem(Properties properties, EntityType<?> entityType, ResourceLocation gemVariantResourceLocation) {
        super(properties);
        this.entityType = entityType;
        this.gemVariantLocation = gemVariantResourceLocation;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();

        if (level.isClientSide())
            return InteractionResult.PASS;

        ItemStack itemStack = context.getItemInHand();

        var clickedFace = context.getClickedFace();
        var clickedPos = context.getClickedPos();
        var blockState = level.getBlockState(clickedPos);

        BlockPos spawnPos;
        if (blockState.getCollisionShape(level, clickedPos).isEmpty())
            spawnPos = clickedPos;
        else
            spawnPos = clickedPos.relative(clickedFace);

        summonGem(itemStack,level,spawnPos,context.getPlayer());

        return InteractionResult.SUCCESS;
    }


    public void summonGem(ItemStack itemStack, Level level, BlockPos spawnPos, @Nullable Player player)
    {
        var gemData = itemStack.getComponents().get(VerneuilDataComponents.GEM_DATA);

        EntitySpawnReason spawnReason;
        Entity entity;
        ServerLevel serverLevel = Objects.requireNonNull(level.getServer()).overworld();

        if (gemData == null)
        { // Gem UUID is not stored in gem item, spawn random gem of gem variant
            spawnReason = EntitySpawnReason.SPAWN_ITEM_USE;
            entity = spawnEntity(level,spawnPos,spawnReason);
            if (entity instanceof AbstractGem gem)
            {
                GemVariant gemVariant = getGemVariant(serverLevel, gemVariantLocation);
                gem.setGemVariant(gemVariant, true, true);
            }
        }
        else
        { // Spawn gem with NBT data
            spawnReason = EntitySpawnReason.LOAD;
            var gemSavedData = serverLevel.getDataStorage().computeIfAbsent(GemSavedData.ID);
            ValueInput valueInput = gemSavedData.getGem(gemData.uuid(), serverLevel.registryAccess());

            // if entity already exists in the world, dont let it spawn and dont give any errors
            if (level.getEntityInAnyDimension(gemData.uuid()) != null)
                return;

            if (valueInput == null && player != null)
            {
                player.displayClientMessage(Component.translatable("verneuil.gemitem.warning"), false);
                return;
            }
            entity = spawnEntity(valueInput, level, spawnPos, spawnReason);
            if (entity != null) {
                level.addFreshEntity(entity);
                gemSavedData.removeGem(entity.getUUID());
            }
        }

        if (entity instanceof LivingEntity livingEntity && gemData != null)
        {
            livingEntity.setHealth(livingEntity.getMaxHealth() * gemData.healthModifier());
            serverLevel.broadcastEntityEvent(livingEntity, (byte)60);
        }

        itemStack.setCount(0);
    }

    private Entity spawnEntity(ValueInput valueInput, Level level, BlockPos pos, EntitySpawnReason spawnReason){
        return EntityType.loadEntityRecursive(entityType,valueInput,level,spawnReason, gem -> {
            gem.snapTo(pos.getX() + 0.5 ,pos.getY(),pos.getZ() + 0.5);
            return gem;
        });
    }

    private Entity spawnEntity(Level level, BlockPos pos, EntitySpawnReason spawnReason){
        var serverLevel = (ServerLevel) level;
        return entityType.spawn(serverLevel, pos, spawnReason);
    }

    private GemVariant getGemVariant(ServerLevel server, ResourceLocation resourceLocation){
        var gemVariant = server.registryAccess().lookupOrThrow(VerneuilDataTypes.GEM_VARIANT).get(resourceLocation);

        return gemVariant.map(Holder.Reference::value).orElse(null);
    }


    @Override
    public boolean canBeHurtBy(@NotNull ItemStack stack, @NotNull DamageSource source) {
		return !source.is(DamageTypeTags.IS_LIGHTNING) && !source.is(DamageTypes.CACTUS);
	}
    @Override
    public boolean hasCustomEntity(@NotNull ItemStack stack) {
        return true;
    }
    @Override
    public Entity createEntity(@NotNull Level level, Entity location, @NotNull ItemStack stack) {
        var entity = new GemItemEntity(level, location.getX(), location.getY(),location.getZ(), stack);
        entity.setDeltaMovement(location.getDeltaMovement());
        entity.setDefaultPickUpDelay();
        return entity;
    }
}
