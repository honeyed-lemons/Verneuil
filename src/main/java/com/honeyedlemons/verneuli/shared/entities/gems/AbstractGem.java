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
import com.honeyedlemons.verneuli.shared.entities.TamableMob;
import com.honeyedlemons.verneuli.shared.util.Palettes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;

public abstract class AbstractGem extends TamableMob implements SmartBrainOwner<AbstractGem>, OwnableEntity {

    public AbstractGem(EntityType<? extends AbstractGem> entityType, Level level) {
        super(entityType, level);
    }


    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.ATTACK_DAMAGE, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.1f);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (!this.level().isClientSide())
        {
            return InteractionResult.PASS;
        }
        if (!this.isTame())
        {
            tame(player,getTameMessage());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public Component getTameMessage()
    {
        return Component.translatable("vernueil.gem.tamemessage", this.getDisplayName());
    }
	//region Brain
	@Override
    protected Brain.@NotNull Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @SuppressWarnings("UnstableApiUsage")
	@Override
    protected void customServerAiStep(@NotNull ServerLevel level) {
        tickBrain(this);
    }

    @Override
    public List<ExtendedSensor<AbstractGem>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(), // This tracks nearby entities
                new HurtBySensor<>()                // This tracks the last damage source and attacker
        );
    }

    @Override
    public BrainActivityGroup<AbstractGem> getCoreTasks() { // These are the tasks that run all the time (usually)
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>());                 // Walk towards the current walk target
    }

    @Override
    public BrainActivityGroup<AbstractGem> getIdleTasks() { // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<AbstractGem>(      // Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new SetPlayerLookTarget<>(),          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()),         // Set a random look target
                new OneRandomBehaviour<>(                 // Run a random task from the below options
                        new SetRandomWalkTarget<>(),          // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60)))); // Do nothing for 1.5->3 seconds
    }

    @Override
    public BrainActivityGroup<AbstractGem> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(), // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>(),      // Set the walk target to the attack target
                new AnimatableMeleeAttack<>(0)); // Melee attack the target if close enough
    }

	//endregion

	//region GemAppearanceData Code
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
	//endregion

	//region GemVariant Code
	public GemVariant getGemVariant()
    {
        if (this.hasData(VerneuilDataAttachments.GEM_VARIANT))
            return this.getData(VerneuilDataAttachments.GEM_VARIANT).value();
        else
            return new GemVariant();
    }

    public void setGemVariant(GemVariant variant, @Nullable Boolean generatePalette, @Nullable Boolean generateVariants) {
        final var registry = registryAccess().lookupOrThrow(VerneuilDataTypes.GEM_VARIANT);
        Holder<GemVariant> holder  = registry.wrapAsHolder(variant);
        this.setData(VerneuilDataAttachments.GEM_VARIANT,holder);
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
	//endregion

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
            Arrays.asList("AbsorptionAmount","Air","DeathTime","FallFlying","Fire","Health","HurtByTimestamp","fall_distance","Motion")
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
        ItemStack gemItem = getGemVariant().gemItem().copy();
        gemItem.remove(VerneuilDataComponents.GEM_DATA);
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
