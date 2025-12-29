package com.honeyedlemons.verneuli.shared.entities.gems;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.shared.data.dataAttachments.GemAppearanceData;
import com.honeyedlemons.verneuli.shared.data.dataAttachments.VerneuilDataAttachments;
import com.honeyedlemons.verneuli.shared.data.dataComponents.VerneuilDataComponents;
import com.honeyedlemons.verneuli.shared.data.dataComponents.GemDataRecord;
import com.honeyedlemons.verneuli.shared.data.dataMaps.EntityGemVariantsDataMap;
import com.honeyedlemons.verneuli.shared.data.dataTypes.GemVariant;
import com.honeyedlemons.verneuli.shared.data.dataTypes.VerneuilDataTypes;
import com.honeyedlemons.verneuli.shared.data.savedData.GemSavedData;
import com.honeyedlemons.verneuli.shared.util.Palettes;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;

public abstract class AbstractGem extends PathfinderMob {

    public AbstractGem(EntityType<? extends AbstractGem> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 32);
    }

    public GemAppearanceData getGemAppearanceData()
    {
        return this.getData(VerneuilDataAttachments.GEM_APPEARANCE_DATA);
    }

    public void setGemAppearanceData(GemAppearanceData appearanceData)
    {
        this.setData(VerneuilDataAttachments.GEM_APPEARANCE_DATA, appearanceData);
    }

    public void addColor(String name, Integer color) {
        var appearanceData = getGemAppearanceData();
        appearanceData.addColorData(name,color);
        this.setGemColors(appearanceData.getColorData());
    }

    public Integer getColor(String name) {
        return getGemAppearanceData().getColorData().get(name);
    }

    public void setGemColors(Map<String,Integer> colorData)
    {
        var appearanceData = getGemAppearanceData();
        appearanceData.setColorData(colorData);
        setGemAppearanceData(appearanceData);
    }

    public Map<String,Integer> getGemColors()
    {
        return getGemAppearanceData().getColorData();
    }

    public void setGemLayerData(Map<String,String> layerData) {
        var appearanceData = getGemAppearanceData();
        appearanceData.setLayerData(layerData);
        setGemAppearanceData(appearanceData);
    }

    public Map<String,String> getGemLayerData()
    {
        return getGemAppearanceData().getLayerData();
    }

    public void AddGemLayer(String layerName, String layerData) {
        var appearanceData = getGemAppearanceData();
        appearanceData.addLayerData(layerName,layerData);
        this.setGemLayerData(appearanceData.getLayerData());
    }

    public GemVariant getGemVariant()
    {
        return this.getData(VerneuilDataAttachments.GEM_VARIANT);
    }

    public void setGemVariant(GemVariant variant, @Nullable Boolean generatePalette, @Nullable Boolean generateVariants) {
        this.setData(VerneuilDataAttachments.GEM_VARIANT,variant);
        ServerLevel serverLevel = (ServerLevel) this.level();
        if (Boolean.TRUE.equals(generatePalette))
            GeneratePaletteColors(serverLevel);
        if (Boolean.TRUE.equals(generateVariants))
            GenerateLayerVariants(serverLevel);
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
        if (this.getGemVariant().palettes().isEmpty())
            return;

        var palettes = this.getGemVariant().palettes().get();

        for (String type : palettes){
            var paletteLocation = Palettes.PaletteLocation(this,type);
            this.addColor(type,Palettes.GenerateColorFromPalette(paletteLocation,random,server));
        }
    }

    public void GenerateLayerVariants(ServerLevelAccessor server){
        var random = server.getRandom();
        if (this.getGemVariant().variants().isEmpty())
            return;

        var variants = this.getGemVariant().variants().get();

        variants.forEach((type, variantList)-> {
            var picked = random.nextInt(variantList.size());
            this.AddGemLayer(type, variantList.get(picked));
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
    public void die(@NotNull DamageSource damageSource) {
        super.die(damageSource);
    }
    @Override
    public void makePoofParticles() {
        for (int i = 0; i < 80; i++) {
            var gaussian = this.random.nextGaussian() * 0.04;
            var scaler1 = 1;
            var scaler2 = 3;
            this.level()
                    .addParticle(ParticleTypes.POOF, this.getRandomX(scaler2) - gaussian * scaler1, this.getRandomY() - gaussian * scaler1, this.getRandomZ(scaler2) - gaussian * scaler1, gaussian, gaussian, gaussian);
        }
    }
    public ItemStack createAndSaveGemItem(ServerLevel serverLevel)
    {
        ItemStack gemItem = getGemVariant().gemItem();
        GemDataRecord gemData = gemItem.get(VerneuilDataComponents.GEM_DATA);
        if (gemData == null)
            gemData = new GemDataRecord(this.getUUID());

        gemData.with(this.getUUID());

        gemItem.set(VerneuilDataComponents.GEM_DATA,gemData);
        saveGem(serverLevel.getServer().overworld());
        return gemItem;
    }

    @Override
    protected void dropEquipment(@NotNull ServerLevel serverLevel) {
        super.dropEquipment(serverLevel);
        serverLevel.broadcastEntityEvent(this, (byte)60);
        var gemItem = createAndSaveGemItem(serverLevel);
        this.spawnAtLocation(serverLevel,gemItem);
    }
    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
            this.remove(Entity.RemovalReason.KILLED);
        }
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
	public @NotNull Component getTypeName(){
		return Component.translatable(this.getGemVariant().translation());
	}

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
        return super.hurtServer(level, damageSource, amount);
    }
}
