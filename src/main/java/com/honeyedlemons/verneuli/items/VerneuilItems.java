package com.honeyedlemons.verneuli.items;

import com.honeyedlemons.verneuli.entities.VerneuilEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

import static com.honeyedlemons.verneuli.Verneuil.MODID;

public class VerneuilItems {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

	public static final DeferredItem<GemItem> AMETHYST_GEM = ITEMS.registerItem(
			"amethyst_gem",
			item -> gemItem(item, VerneuilEntities.QUARTZ.get(), "amethyst"));
	public static final DeferredItem<GemItem> CITRINE_GEM = ITEMS.registerItem(
			"citrine_gem",
			item -> gemItem(item, VerneuilEntities.QUARTZ.get(), "citrine"));
	public static final DeferredItem<GemItem> ROSE_QUARTZ_GEM = ITEMS.registerItem(
			"rose_quartz_gem",
			item -> gemItem(item, VerneuilEntities.QUARTZ.get(), "rose_quartz"));
	public static final DeferredItem<GemItem> CARNELIAN_GEM = ITEMS.registerItem(
			"carnelian_gem",
			item -> gemItem(item, VerneuilEntities.QUARTZ.get(), "carnelian"));
	public static final DeferredItem<GemItem> RED_STRIPED_JASPER_GEM = ITEMS.registerItem(
			"red_striped_jasper_gem",
			item -> gemItem(item, VerneuilEntities.QUARTZ.get(), "red_striped_jasper"));
	public static final DeferredItem<GemItem> BIGGS_JASPER_GEM = ITEMS.registerItem(
			"biggs_jasper_gem",
			item -> gemItem(item, VerneuilEntities.QUARTZ.get(), "biggs_jasper"));
	public static final DeferredItem<GemItem> OCEAN_JASPER_GEM = ITEMS.registerItem(
			"ocean_jasper_gem",
			item -> gemItem(item, VerneuilEntities.QUARTZ.get(), "ocean_jasper"));
	public static final DeferredItem<GemItem> ZEBRA_JASPER_GEM = ITEMS.registerItem(
			"zebra_jasper_gem",
			item -> gemItem(item, VerneuilEntities.QUARTZ.get(), "zebra_jasper"));

	public static GemItem gemItem(Item.Properties properties, EntityType<?> entityType, String variant) {
		var resourceLocation = ResourceLocation.fromNamespaceAndPath(MODID, entityType.toShortString() + "/" + variant);
		return new GemItem(properties.stacksTo(1).fireResistant(), entityType, resourceLocation);
	}

	public static List<GemItem> gems() {
		ArrayList<GemItem> list = new ArrayList<>();
		ITEMS.getEntries().forEach((item) -> {
			if (item.get() instanceof GemItem gemItem)
				list.add(gemItem);
		});
		return list;
	}
}
