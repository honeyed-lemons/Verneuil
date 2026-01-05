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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.Tags;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.BowAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.LeapAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowEntity;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowOwner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.*;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyItemsSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class AbstractGem extends TamableMob implements SmartBrainOwner<AbstractGem>, OwnableEntity, RangedAttackMob {

    public AbstractGem(EntityType<? extends AbstractGem> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);
    }


    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.ATTACK_DAMAGE, 1)
                .add(Attributes.MAX_HEALTH,20)
                .add(Attributes.MOVEMENT_SPEED, 0.35F);
    }

	//region SaveData
	@Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setCanPickUpLoot(input.getBooleanOr("CanPickUpLoot", true));
    }
	//endregion

	//region Interactions
	@Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (this.level().isClientSide() || hand.equals(InteractionHand.OFF_HAND))
            return InteractionResult.SUCCESS_SERVER;

        ServerLevel serverLevel = (ServerLevel) this.level();

        if (!player.isCrouching())
        {
            if (!this.isTame())
            {
                tame(player,getTameMessage());
                this.talk();
                return InteractionResult.SUCCESS;
            }
			if (this.isOwnedBy(player))
			{
				if (item instanceof DyeItem dyeItem)
				{
					ApplyDye("insignia",itemstack,dyeItem,player);
					return InteractionResult.CONSUME;
				}
                if (item instanceof ShearsItem )
                {
                    cycleGemLayer("hair");
                    this.playSound(SoundEvents.SHEARS_SNIP, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
				if (itemEquippable(itemstack))
				{
					ItemStack equipped = equipItemIfPossible(serverLevel,itemstack.copy());
					if (!equipped.isEmpty())
					{
						itemstack.consume(1,player);
						return InteractionResult.CONSUME;
					}
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
                    return InteractionResult.CONSUME;
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
                this.talk();
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    public boolean itemEquippable(ItemStack item) {
        return item.getTags().anyMatch(key ->
                (key == (Tags.Items.ARMORS) && canWearArmor()) || key == Tags.Items.TOOLS);
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        return stack.getTags().anyMatch(key -> key != Tags.Items.ARMORS);
    }

    @Override
    public boolean wantsToPickUp(ServerLevel serverLevel, ItemStack stack) {
        return itemEquippable(stack) && canHoldItem(stack) && this.isMovementType(0);
    }

    public boolean wantsToPickUp(ItemStack stack) {
        return itemEquippable(stack) && canHoldItem(stack) && this.isMovementType(0);
    }

    @Override
    protected void hurtArmor(DamageSource damageSource, float damage) {
        this.doHurtEquipment(damageSource, damage, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);
    }
    @Override
    protected void hurtHelmet(DamageSource damageSource, float damage) {
        this.doHurtEquipment(damageSource, damage, EquipmentSlot.HEAD);
    }

    abstract boolean canWearArmor();
    public void ApplyDye(String paletteName, ItemStack stack, DyeItem dyeItem, Player player)
    {
        var color = dyeItem.getDyeColor().getTextureDiffuseColor();
        addColor(paletteName,color);
        stack.consume(1, player);
    }

    public Component getTameMessage()
    {
        return Component.translatable("verneuil.gem.tamemessage", this.getDisplayName());
    }
	//endregion

	//region Brain
	@Override
    protected Brain.@NotNull Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    @SuppressWarnings("UnstableApiUsage")
	@Override
    protected void customServerAiStep(@NotNull ServerLevel level) {
        if (!this.level().isClientSide() && this.isAlive() && this.tickCount % 200 == 0) {
            this.heal(1.0F);
        }
        tickBrain(this);
    }

    public void talk()
    {
        this.playTalkSound();
    }

    @Override
    public List<ExtendedSensor<AbstractGem>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<AbstractGem>()
                        .setRadius(12f),
                new HurtBySensor<>(),
                new NearbyItemsSensor<AbstractGem>()
                        .setRadius(6)
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
                        new FollowEntity<>()
                                .following(AbstractGem::followItem)
                                .stopFollowingWithin(1)
                        .cooldownForBetween(30,60),
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

	//region Combat
	@Override
    public BrainActivityGroup<AbstractGem> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new FirstApplicableBehaviour<>(
                        new StrafeTarget<>()
                                .stopStrafingWhen(entity -> !isHoldingBow(entity) || !entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET))
                                .startCondition(AbstractGem::isHoldingBow)
                                .whenStopping(Mob::stopInPlace)
                                .whenStarting(gem -> this.setAggressive(true)),
                        new SetWalkTargetToAttackTarget<>()
                ),
                new LeapAtTarget<>(20)
                        .verticalJumpStrength((mod, gem) -> 0.7f)
                        .jumpStrength((mob, gem) -> 0.7f)
                        .startCondition(AbstractGem::isHoldingMace),
                new FirstApplicableBehaviour<>(
                        new BowAttack<>(20)
                                .whenStopping(gem -> this.setAggressive(false))
                                .startCondition(AbstractGem::isHoldingBow),
                        new AnimatableMeleeAttack<>(0)
                )
        );
    }
    public abstract Boolean canFight();

    public boolean canAttack(@NotNull LivingEntity entity)
    {
        return this.canFight() && !this.isOwnedBy(entity) && !this.doesShareOwner(entity) && (entity instanceof Enemy || this.getBrain().isMemoryValue(MemoryModuleType.HURT_BY_ENTITY, entity));
    }

    public static ItemEntity followItem(@NotNull LivingEntity entity)
    {
        var memory = entity.getBrain().getMemory(SBLMemoryTypes.NEARBY_ITEMS.get());
		return memory.map(List::getFirst).orElse(null);
	}
    private static boolean isHoldingBow(LivingEntity livingEntity) {
        return livingEntity.isHolding(stack -> stack.getItem() instanceof BowItem);
    }
    private static boolean isHoldingMace(LivingEntity livingEntity) {
        return livingEntity.isHolding(stack -> stack.getItem() instanceof MaceItem);
    }
    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float distanceFactor) {
        ItemStack weapon = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, (item) -> item instanceof BowItem));
        ItemStack projectile = this.getProjectile(weapon);
        AbstractArrow abstractarrow = this.getArrow(projectile, distanceFactor, weapon);
        Item item = weapon.getItem();

        if (item instanceof ProjectileWeaponItem weaponItem) {
            abstractarrow = weaponItem.customArrow(abstractarrow, projectile, weapon);
        }

		Level level = this.level();
        if (level instanceof ServerLevel serverlevel) {
            double d0 = target.getX() - this.getX();
            double d1 = target.getY(0.3) - abstractarrow.getY();
            double d2 = target.getZ() - this.getZ();
			double d3 = Math.sqrt(d0 * d0 + d2 * d2);
			Projectile.spawnProjectileUsingShoot(abstractarrow, serverlevel, projectile, d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - serverlevel.getDifficulty().getId() * 4));
        }

        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, (1.0F / this.getRandom().nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
    }

    protected AbstractArrow getArrow(ItemStack arrow, float velocity, @Nullable ItemStack weapon) {
        return ProjectileUtil.getMobArrow(this, arrow, velocity, weapon);
    }
	//endregion
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

    public void addGemLayer(String layerName, String layerData) {
        var appearanceData = getGemAppearanceData();
        appearanceData.addLayerData(layerName,layerData);
        this.setGemLayerData(appearanceData.getLayerData());
    }

    public String getGemLayerVariant(String layerName)
    {
        var layerData = getGemAppearanceData().getLayerData();
        return layerData.get(layerName);
    }

    public void cycleGemLayer(String layerName)
    {
        var layerData = new HashMap<>(getGemAppearanceData().getLayerData());
        if (getGemVariant().variants().isEmpty())
            return;

        var variantList = getGemVariant().variants().get().get(layerName);

        var layerIndex = variantList.indexOf(getGemLayerVariant(layerName));

        if (layerIndex != variantList.size() - 1)
			layerIndex++;
        else
            layerIndex = 0;

        var selectedLayer = variantList.get(layerIndex);

        layerData.put(layerName,selectedLayer);

        this.setGemLayerData(layerData);
    }

    public void generatePaletteColors(ServerLevelAccessor server) {
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

    public void generateLayerVariants(ServerLevelAccessor server){
        var random = server.getRandom();
        if (this.getGemVariant().variants().isEmpty())
            return;

        var variants = this.getGemVariant().variants().get();

        variants.forEach((type, variantList)-> {
            var picked = random.nextInt(variantList.size());
            this.addGemLayer(type, variantList.get(picked));
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
            generatePaletteColors(serverLevel);
        if (Boolean.TRUE.equals(generateVariants))
            generateLayerVariants(serverLevel);
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

    public void generateGemVariant(ServerLevelAccessor server){
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


	//region Sound

    public SoundEvent getTalkSound() {
        var talkSound = this.getGemVariant().talkSound().orElse(null);
        return talkSound != null ? talkSound.value() : null;
    }

    public void playTalkSound()
    {
        this.playSound(getTalkSound(), 1.0F, this.getVoicePitch());
    }

	//endregion

    //region Death
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
    protected void dropAllDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource damageSource) {
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
                this.generateGemVariant(server);
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
	//endregion
}
