package com.honeyedlemons.verneuli.shared.entities.gems;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.shared.data.datacomponents.GemDataRecord;
import com.honeyedlemons.verneuli.shared.data.datacomponents.VerneuilDataComponents;
import com.honeyedlemons.verneuli.shared.data.datamaps.EntityGemVariantsDataMap;
import com.honeyedlemons.verneuli.shared.data.datatypes.GemVariant;
import com.honeyedlemons.verneuli.shared.data.datatypes.VerneuilDataTypes;
import com.honeyedlemons.verneuli.shared.data.saveddata.GemSavedData;
import com.honeyedlemons.verneuli.shared.data.dataserializers.GemLayerVariantDataSerializer;
import com.honeyedlemons.verneuli.shared.data.dataserializers.VerneuilDataSerializers;
import com.honeyedlemons.verneuli.shared.data.dataserializers.GemColorDataSerializer;
import com.honeyedlemons.verneuli.shared.util.Palettes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class AbstractGem extends PathfinderMob {

    public AbstractGem(EntityType<? extends AbstractGem> entityType, Level level) {
        super(entityType, level);
    }

    private static final EntityDataAccessor<Map<String,Integer>> GEM_COLORS = SynchedEntityData.defineId(
            AbstractGem.class, VerneuilDataSerializers.GEM_COLORS.get()
    );

    private static final EntityDataAccessor<Map<String,String>> GEM_LAYER_VARIANTS = SynchedEntityData.defineId(
            AbstractGem.class, VerneuilDataSerializers.GEM_LAYER_VARIANTS.get()
    );
    private static final EntityDataAccessor<GemVariant> GEM_TYPE_VARIANT = SynchedEntityData.defineId(
            AbstractGem.class, VerneuilDataSerializers.GEM_VARIANT.get()
    );

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 32);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setGemColors(input.read("GemColors", GemColorDataSerializer.CODEC).orElse(new HashMap<>()));
        this.setGemLayerVariants(input.read("GemLayerVariants", GemLayerVariantDataSerializer.CODEC).orElse(new HashMap<>()));
        this.setGemVariant(input.read("GemVariant", GemVariant.CODEC).orElse(new GemVariant()), false, false);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("GemColors", GemColorDataSerializer.CODEC,getGemColors());
        output.store("GemLayerVariants", GemLayerVariantDataSerializer.CODEC,getGemLayerVariants());
        output.store("GemVariant",GemVariant.CODEC,getGemVariant());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(GEM_COLORS, new HashMap<>());
        builder.define(GEM_LAYER_VARIANTS, new HashMap<>());
        builder.define(GEM_TYPE_VARIANT, new GemVariant());
    }

    public void addColor(String name, Integer color) {
        var map = this.entityData.get(GEM_COLORS);
        map.put(name, color);
        this.setGemColors(map);
    }

    public Integer getColor(String name) {
        var map = this.entityData.get(GEM_COLORS);
        return map.getOrDefault(name, Color.WHITE.getRGB());
    }

    public void setGemColors(Map<String,Integer> map)
    {
        this.entityData.set(GEM_COLORS,map,true);
    }

    public Map<String,Integer> getGemColors()
    {
        return this.entityData.get(GEM_COLORS);
    }

    public void setGemLayerVariants(Map<String,String> map) {
        this.entityData.set(GEM_LAYER_VARIANTS,map,true);
    }

    public Map<String,String> getGemLayerVariants()
    {
        return this.entityData.get(GEM_LAYER_VARIANTS);
    }
    public void AddGemLayerVariants(String name, String data) {
        var map = this.entityData.get(GEM_LAYER_VARIANTS);
        map.put(name, data);
        this.setGemLayerVariants(map);
    }

    public void RemoveGemLayerVariants(String name) {
        var map = this.entityData.get(GEM_LAYER_VARIANTS);
        map.remove(name);
        this.setGemLayerVariants(map);
    }

    public void setGemVariant(GemVariant variant, @Nullable Boolean generatePalette, @Nullable Boolean generateVariants) {
        this.entityData.set(GEM_TYPE_VARIANT,variant);
        ServerLevel serverLevel = (ServerLevel) this.level();
        if (Boolean.TRUE.equals(generatePalette))
            GeneratePaletteColors(serverLevel);
        if (Boolean.TRUE.equals(generateVariants))
            GenerateLayerVariants(serverLevel);
    }

    public GemVariant getGemVariant() {
        return this.entityData.get(GEM_TYPE_VARIANT);
    }

    public GemVariant getGemVariant(String name) {
        var resourceLocation = ResourceLocation.fromNamespaceAndPath(Verneuil.MODID,
                this.getType().toShortString()+"/"+name);

        return getGemVariant(resourceLocation);
    }

    public GemVariant getGemVariant(ResourceLocation resourceLocation) {

        var gemVariant = this.registryAccess().lookupOrThrow(VerneuilDataTypes.GEM_VARIANT).get(resourceLocation);
        return gemVariant.map(Holder.Reference::value).orElse(null);
    }

    public GemVariant getGemVariant(ResourceKey<GemVariant> resourceKey) {

        var gemVariant = this.registryAccess().lookupOrThrow(VerneuilDataTypes.GEM_VARIANT).get(resourceKey);
        return gemVariant.map(Holder.Reference::value).orElse(null);
    }

    public void GeneratePaletteColors(ServerLevelAccessor server) {
        var random = server.getRandom();

        for (String type : this.getGemVariant().palettes()){
            var paletteLocation = Palettes.PaletteLocation(this,type);
            this.addColor(type,Palettes.GenerateColorFromPalette(paletteLocation,random,server));
        }
    }

    public void GenerateLayerVariants(ServerLevelAccessor server){
        var random = server.getRandom();

        this.getGemVariant().variants().forEach((type, variants)-> {
            var picked = random.nextInt(variants.size());
            this.AddGemLayerVariants(type, variants.get(picked));
        });
    }

    public void GenerateGemVariant(ServerLevelAccessor server){
        var random = server.getRandom();

        final var registry = registryAccess().lookupOrThrow(Registries.ENTITY_TYPE);
        Holder<EntityType<?>> holder  = registry.wrapAsHolder(this.getType());

        var gemVariantData =  holder.getData(EntityGemVariantsDataMap.GEM_VARIANTS);
        List<ResourceLocation> gemVariantList;
        if (gemVariantData == null) {
            return;
        }
        else gemVariantList = gemVariantData.gemVariants();

        var picked = gemVariantList.get(random.nextInt(gemVariantList.size()));
        setGemVariant(getGemVariant(picked), true, true);
    }

    public void saveGem(ServerLevel server)
    {
        var savedData = server.getDataStorage().computeIfAbsent(GemSavedData.ID);
        try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(this.problemPath(), Verneuil.LOGGER)) {
            var tagValueOutput = Optional.of(TagValueOutput.createWithContext(problemreporter$scopedcollector, this.registryAccess()));

            this.saveWithoutId(tagValueOutput.get());

            for (String data : dataToDiscard)
                tagValueOutput.get().discard(data);

            savedData.addGem(this.getUUID(), tagValueOutput.get());
        }
    }

    public List<String> dataToDiscard = new ArrayList<>(
            Arrays.asList("AbsorptionAmount","Air","DeathTime","FallFlying","Fire","Health","HurtByTimestamp","fall_distance")
    );

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        Poof();
        super.remove(removalReason);
    }
    public void Poof()
    {
        ItemStack gemItem = getGemVariant().gemItem();
        GemDataRecord gemData = gemItem.get(VerneuilDataComponents.GEM_DATA);
        if (gemData == null)
            gemData = new GemDataRecord(this.getUUID());

        gemData.with(this.getUUID());

        gemItem.set(VerneuilDataComponents.GEM_DATA,gemData);
        saveGem(Objects.requireNonNull(this.level().getServer()).overworld());
        drop(gemItem,false,false);
    }
    @SuppressWarnings("deprecation")
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor server, @NotNull DifficultyInstance difficulty, @NotNull EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnGroupData) {

        if (spawnReason != EntitySpawnReason.LOAD)
        {
            GemVariant gemVariant = this.getGemVariant();
            if (gemVariant.type() == null)
                GenerateGemVariant(server);
        }

        return super.finalizeSpawn(server, difficulty, spawnReason, spawnGroupData);
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
        return super.hurtServer(level, damageSource, amount);
    }
}
