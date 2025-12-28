package com.honeyedlemons.verneuli.shared.items;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.shared.entities.VerneuilEntities;
import com.honeyedlemons.verneuli.shared.entities.gems.AbstractGem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.honeyedlemons.verneuli.Verneuil.MODID;

public class VerneuilItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<GemItem> QUARTZ_GEM_AMETHYST = ITEMS.registerItem("amethyst_gem", item ->
            gemItem(item, VerneuilEntities.QUARTZ.get(),"amethyst"));

    public static final DeferredItem<GemItem> QUARTZ_GEM_RED_STRIPED_JASPER = ITEMS.registerItem("red_striped_jasper_gem", item ->
            gemItem(item, VerneuilEntities.QUARTZ.get(),"red_striped_jasper"));

    public static GemItem gemItem (Item.Properties properties, EntityType<?> entityType, String variant){
        var resourceLocation = ResourceLocation.fromNamespaceAndPath(MODID,entityType.toShortString()+"/"+variant);
        return new GemItem(properties.stacksTo(1),entityType,resourceLocation);
    }
}
