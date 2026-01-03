package com.honeyedlemons.verneuli.entities;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.entities.gems.Quartz;
import com.honeyedlemons.verneuli.entities.items.GemItemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.honeyedlemons.verneuli.Verneuil.MODID;
@EventBusSubscriber(modid = Verneuil.MODID)
public class VerneuilEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(MODID);

    public static final Supplier<EntityType<Quartz>> QUARTZ = ENTITY_TYPES.registerEntityType(
            "quartz", Quartz::new, MobCategory.CREATURE,
            builder -> builder.eyeHeight(1.72f));

    public static final Supplier<EntityType<GemItemEntity>> GEM_ITEM_ENTITY = ENTITY_TYPES.registerEntityType(
            "gem_item", GemItemEntity::new, MobCategory.MISC,
            builder -> builder
                    .noLootTable()
                    .sized(0.25F, 0.25F)
                    .eyeHeight(0.2125F)
                    .clientTrackingRange(6)
                    .updateInterval(20));


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(QUARTZ.get(), Quartz.createAttributes().build());
    }
}
