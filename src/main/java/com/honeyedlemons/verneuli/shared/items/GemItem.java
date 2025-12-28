package com.honeyedlemons.verneuli.shared.items;

import com.honeyedlemons.verneuli.shared.data.datacomponents.VerneuilDataComponents;
import com.honeyedlemons.verneuli.shared.data.datatypes.GemVariant;
import com.honeyedlemons.verneuli.shared.data.datatypes.VerneuilDataTypes;
import com.honeyedlemons.verneuli.shared.data.saveddata.GemSavedData;
import com.honeyedlemons.verneuli.shared.entities.gems.AbstractGem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;

public class GemItem extends Item {

    private final ResourceLocation gemVariantLocation;
    private final EntityType<?> entityType;

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


        var gemData = itemStack.getComponents().get(VerneuilDataComponents.GEM_DATA);

        EntitySpawnReason spawnReason;
        Entity entity;
        ServerLevel serverLevel = level.getServer().overworld();
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
            if (valueInput.read("GemVariant", GemVariant.CODEC).isEmpty())
            {
                return InteractionResult.FAIL;
            }
            entity = spawnEntity(valueInput, level, spawnPos, spawnReason);
            if (entity != null) {
                gemSavedData.removeGem(entity.getUUID());
                level.addFreshEntity(entity);
            }
        }
        itemStack.consume(1, null);
        return InteractionResult.SUCCESS;
    }

    public Entity spawnEntity(ValueInput valueInput, Level level, BlockPos pos, EntitySpawnReason spawnReason){
        return EntityType.loadEntityRecursive(entityType,valueInput,level,spawnReason, gem -> {
            gem.snapTo(pos.getX(),pos.getY(),pos.getZ());
            return gem;
        });
    }

    public Entity spawnEntity(Level level, BlockPos pos, EntitySpawnReason spawnReason){
        var serverLevel = (ServerLevel) level;
        return entityType.spawn(serverLevel, pos, spawnReason);
    }

    public GemVariant getGemVariant(ServerLevel server, ResourceLocation resourceLocation){
        var gemVariant = server.registryAccess().lookupOrThrow(VerneuilDataTypes.GEM_VARIANT).get(resourceLocation);

        return gemVariant.map(Holder.Reference::value).orElse(null);
    }
}
