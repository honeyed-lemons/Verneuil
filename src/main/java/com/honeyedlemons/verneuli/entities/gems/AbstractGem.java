package com.honeyedlemons.verneuli.entities.gems;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.data.dataAttachments.GemAppearanceData;
import com.honeyedlemons.verneuli.data.dataAttachments.VerneuilDataAttachments;
import com.honeyedlemons.verneuli.data.dataComponents.VerneuilDataComponents;
import com.honeyedlemons.verneuli.data.dataComponents.GemDataRecord;
import com.honeyedlemons.verneuli.data.dataMaps.EntityGemVariantsDataMap;
import com.honeyedlemons.verneuli.data.dataTypes.GemVariant;
import com.honeyedlemons.verneuli.data.dataTypes.PaletteData;
import com.honeyedlemons.verneuli.data.dataTypes.VerneuilDataTypes;
import com.honeyedlemons.verneuli.data.savedData.GemSavedData;
import com.honeyedlemons.verneuli.entities.TamableMob;
import com.honeyedlemons.verneuli.util.Palettes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.common.Tags;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowOwner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.*;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
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
                .add(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (this.level().isClientSide() || hand.equals(InteractionHand.OFF_HAND))
        {
            return InteractionResult.PASS;
        }
        ServerLevel serverLevel = (ServerLevel) this.level();

        if (!player.isCrouching())
        {
            if (!this.isTame())
            {
                tame(player,getTameMessage());
                return InteractionResult.SUCCESS;
            }
            else if (this.isOwnedBy(player))
            {
                if (item instanceof DyeItem dyeItem)
                {
                    ApplyDye("insignia",itemstack,dyeItem,player);
                    return InteractionResult.SUCCESS;
                }
                if ((itemEquippable(itemstack)) && equipItemIfPossible(serverLevel,itemstack) != ItemStack.EMPTY)
                {
                    itemstack.consume(1,player);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        else
        {
            if (this.isOwnedBy(player))
            {
                if (item instanceof DyeItem dyeItem)
                {
                    ApplyDye("insignia",itemstack,dyeItem,player);
                    return InteractionResult.SUCCESS;
                }
                this.cycleMovementType();
                Component component;
				byte movementType = getMovementType();
				component = switch (movementType) {
					case 0 -> Component.translatable("verneuil.gem.movementype.0", this.getDisplayName());
					case 1 -> Component.translatable("verneuil.gem.movementype.1", this.getDisplayName());
					case 2 -> Component.translatable("verneuil.gem.movementype.2", this.getDisplayName());
					default -> null;
				};
                if (component != null)
                    player.displayClientMessage(component, false);
            }
        }

        return InteractionResult.PASS;
    }

    public boolean itemEquippable(ItemStack item)
    {
        return item.getTags().anyMatch(key ->
                (key == (Tags.Items.ARMORS) && canWearArmor()) || key == Tags.Items.TOOLS);
    }
    abstract boolean canWearArmor();
    public void ApplyDye(String paletteName, ItemStack stack, DyeItem dyeItem, Player player)
    {
        var color = dyeItem.getDyeColor().getTextureDiffuseColor();
        addColor(paletteName,color);
        stack.consume(1, player);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    public Component getTameMessage()
    {
        return Component.translatable("verneuil.gem.tamemessage", this.getDisplayName());
    }

    public abstract Boolean canFight();

	//region Brain
	@Override
    protected Brain.@NotNull Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @SuppressWarnings("UnstableApiUsage")
	@Override
    protected void customServerAiStep(@NotNull ServerLevel level) {
        if (!this.level().isClientSide() && this.isAlive() && this.tickCount % 200 == 0) {
            this.heal(1.0F);
        }
        tickBrain(this);
    }

    @Override
    public List<ExtendedSensor<AbstractGem>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<AbstractGem>()
                        .setRadius(12f),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<AbstractGem> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>()
                        .runFor(entity -> entity.getRandom().nextIntBetweenInclusive(40, 100))
                        .whenStopping(entity -> this.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET)),
                new MoveToWalkTarget<>());
    }

    @Override
    public BrainActivityGroup<AbstractGem> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<AbstractGem>(
                        new SetRandomLookTarget<>())
                        .cooldownForBetween(30,60),
                        new SetPlayerLookTarget<>(),
                        new FollowOwner<>()
                        .teleportToTargetAfter(32)
                        .startCondition(this::isFollowing),
                        new TargetOrRetaliate<>()
                                .isAllyIf((attacker,us) -> this.doesShareOwner(attacker))
                                .attackablePredicate(this::canAttack),
                        new MoveToWalkTarget<>(),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>()
                                .speedModifier(0.7f)
                                .setRadius(5)
                                .cooldownForBetween(100,200)
                                .startCondition(this::isWandering),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))));
    }

    @Override
    public BrainActivityGroup<AbstractGem> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(), // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>(),      // Set the walk target to the attack target
                new AnimatableMeleeAttack<>(0)); // Melee attack the target if close enough
    }

    public boolean canAttack(@NotNull LivingEntity entity)
    {
        return this.canFight() && !this.isOwnedBy(entity) && !this.doesShareOwner(entity) && (entity instanceof Enemy || this.getBrain().isMemoryValue(MemoryModuleType.HURT_BY_ENTITY, entity));
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

        for (PaletteData paletteData : palettes){
            if (paletteData.random()) {
                var paletteLocation = Palettes.PaletteLocation(this, paletteData.paletteName());
                this.addColor(paletteData.paletteName(), Palettes.GenerateColorFromPalette(paletteLocation, random, server));
            }
            else
            {
                var color = paletteData.defaultColor().isPresent() ? paletteData.defaultColor().get() : Color.white.getRGB();
                this.addColor(paletteData.paletteName(),color);
            }
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
    public ItemStack createAndSaveGemItem(ServerLevel serverLevel, DamageSource damageSource)
    {
        ItemStack gemItem = getGemVariant().gemItem().copy();
        gemItem.remove(VerneuilDataComponents.GEM_DATA);
        GemDataRecord gemData = gemItem.get(VerneuilDataComponents.GEM_DATA);

        var healthModifier = damageSource.getEntity() == this.getOwner() ? 1f : 0.25f;

        if (gemData == null)
            gemData = new GemDataRecord(this.getUUID(),healthModifier);

        gemData.with(this.getUUID(),healthModifier);

        gemItem.set(VerneuilDataComponents.GEM_DATA,gemData);
        saveGem(serverLevel.getServer().overworld());
        return gemItem;
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel level, DamageSource damageSource) {
        super.dropAllDeathLoot(level,damageSource);
        level.broadcastEntityEvent(this, (byte)60);
        var gemItem = createAndSaveGemItem(level, damageSource);
        this.spawnAtLocation(level,gemItem, this.getBbHeight()/2);
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
            this.remove(RemovalReason.KILLED);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor server, @NotNull DifficultyInstance difficulty, @NotNull EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnGroupData) {

        if (spawnReason != EntitySpawnReason.LOAD)
        {
            GemVariant gemVariant = this.getGemVariant();
            if (gemVariant.type() == null)
                this.GenerateGemVariant(server);
        }

        return super.finalizeSpawn(server, difficulty, spawnReason, spawnGroupData);
    }

	@Override
	public @NotNull Component getTypeName(){
		return Component.translatable(this.getGemVariant().translation());
	}

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
        if (damageSource.is(Tags.DamageTypes.IS_POISON) || damageSource.is(DamageTypeTags.IS_DROWNING) || damageSource.is(DamageTypeTags.IS_FALL))
        {
            return false;
        }

        boolean flag = super.hurtServer(level, damageSource, amount);

        var entity = damageSource.getEntity();

        if (entity instanceof ServerPlayer player && this.getOwner() == player)
        {
            if (player.isCrouching())
            {
                super.hurtServer(level, damageSource, 1000);
                return true;
            }
        }

        return flag;
    }
}
