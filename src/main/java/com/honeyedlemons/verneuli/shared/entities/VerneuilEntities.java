package com.honeyedlemons.verneuli.shared.entities;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.shared.entities.gems.Quartz;
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
            builder -> builder);

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(QUARTZ.get(), Quartz.createAttributes().build());
    }
}
